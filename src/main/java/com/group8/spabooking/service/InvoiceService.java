package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.InvoicePaymentRequest;
import com.group8.spabooking.dto.request.InvoiceRequest;
import com.group8.spabooking.dto.request.InvoiceStatusRequest;
import com.group8.spabooking.dto.response.InvoiceResponse;
import com.group8.spabooking.entity.Appointment;
import com.group8.spabooking.entity.AppointmentService;
import com.group8.spabooking.entity.Invoice;
import com.group8.spabooking.exception.BadRequestException;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.AppointmentServiceRepository;
import com.group8.spabooking.repository.InvoiceRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private static final String UNPAID_STATUS = "Chưa thanh toán";
    private static final String PAID_STATUS = "Đã thanh toán";
    private static final Set<String> VALID_PAYMENT_STATUSES = Set.of(
            "Chưa thanh toán",
            "Đã thanh toán",
            "Đã hủy",
            "Hoàn tiền");
    private static final Set<String> VALID_PAYMENT_METHODS = Set.of(
            "Tiền mặt",
            "Chuyển khoản",
            "Thẻ ngân hàng",
            "Ví điện tử");

    private final InvoiceRepository invoiceRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final AppointmentManagerService appointmentManagerService;

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAll() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findById(Long id) {
        return InvoiceResponse.from(getInvoice(id));
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findByAppointmentId(Long appointmentId) {
        return InvoiceResponse.from(invoiceRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn")));
    }

    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        if (invoiceRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new BadRequestException("Lịch hẹn này đã có hóa đơn");
        }

        Appointment appointment = appointmentManagerService.getAppointment(request.getAppointmentId());
        BigDecimal totalAmount = calculateTotalAmount(appointment.getId());
        BigDecimal discountAmount = discountOrZero(request.getDiscountAmount());
        validateDiscount(totalAmount, discountAmount);
        validatePaymentMethodIfPresent(request.getPaymentMethod());

        Invoice invoice = Invoice.builder()
                .appointment(appointment)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(UNPAID_STATUS)
                .createdAt(LocalDateTime.now())
                .build();

        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    @Transactional
    public InvoiceResponse pay(Long id, InvoicePaymentRequest request) {
        validatePaymentMethod(request.getPaymentMethod());
        Invoice invoice = getInvoice(id);
        invoice.setPaymentMethod(request.getPaymentMethod());
        invoice.setPaymentStatus(PAID_STATUS);
        invoice.setPaidAt(LocalDateTime.now());
        return InvoiceResponse.from(invoice);
    }

    @Transactional
    public InvoiceResponse updateStatus(Long id, InvoiceStatusRequest request) {
        String paymentStatus = request.getPaymentStatus().trim();
        if (!VALID_PAYMENT_STATUSES.contains(paymentStatus)) {
            throw new BadRequestException("Trạng thái thanh toán không hợp lệ");
        }

        Invoice invoice = getInvoice(id);
        invoice.setPaymentStatus(paymentStatus);
        if (PAID_STATUS.equals(paymentStatus) && invoice.getPaidAt() == null) {
            invoice.setPaidAt(LocalDateTime.now());
        }
        if (!PAID_STATUS.equals(paymentStatus)) {
            invoice.setPaidAt(null);
        }
        return InvoiceResponse.from(invoice);
    }

    @Transactional
    public void delete(Long id) {
        Invoice invoice = getInvoice(id);
        invoiceRepository.delete(invoice);
    }

    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn"));
    }

    private BigDecimal calculateTotalAmount(Long appointmentId) {
        List<AppointmentService> services = appointmentServiceRepository.findByAppointmentId(appointmentId);
        if (services.isEmpty()) {
            throw new BadRequestException("Lịch hẹn chưa có dịch vụ để tạo hóa đơn");
        }

        return services.stream()
                .map(AppointmentService::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal discountOrZero(BigDecimal discountAmount) {
        return discountAmount == null ? BigDecimal.ZERO : discountAmount;
    }

    private void validateDiscount(BigDecimal totalAmount, BigDecimal discountAmount) {
        if (discountAmount.compareTo(totalAmount) > 0) {
            throw new BadRequestException("Giảm giá không được lớn hơn tổng tiền");
        }
    }

    private void validatePaymentMethodIfPresent(String paymentMethod) {
        if (paymentMethod != null && !paymentMethod.isBlank()) {
            validatePaymentMethod(paymentMethod);
        }
    }

    private void validatePaymentMethod(String paymentMethod) {
        if (!VALID_PAYMENT_METHODS.contains(paymentMethod.trim())) {
            throw new BadRequestException("Phương thức thanh toán không hợp lệ");
        }
    }
}
