package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.AppointmentRequest;
import com.group8.spabooking.dto.request.BusinessAppointmentRequest;
import com.group8.spabooking.dto.request.BusinessInvoicePaymentRequest;
import com.group8.spabooking.dto.request.BusinessInvoiceRequest;
import com.group8.spabooking.dto.request.BusinessPaymentStatusRequest;
import com.group8.spabooking.dto.request.BusinessSmsReminderRequest;
import com.group8.spabooking.dto.request.InvoiceRequest;
import com.group8.spabooking.dto.request.LoginRequest;
import com.group8.spabooking.dto.request.SmsReminderRequest;
import com.group8.spabooking.dto.response.AppointmentResponse;
import com.group8.spabooking.dto.response.InvoiceResponse;
import com.group8.spabooking.dto.response.LoginResponse;
import com.group8.spabooking.dto.response.SmsReminderResponse;
import com.group8.spabooking.entity.Appointment;
import com.group8.spabooking.entity.Employee;
import com.group8.spabooking.exception.BadRequestException;
import com.group8.spabooking.repository.AppointmentRepository;
import com.group8.spabooking.repository.EmployeeScheduleRepository;
import java.time.LocalTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessFlowService {

    private final AuthService authService;
    private final CurrentUserService currentUserService;
    private final AppointmentManagerService appointmentManagerService;
    private final InvoiceService invoiceService;
    private final SmsReminderService smsReminderService;
    private final SpaService spaService;
    private final EmployeeService employeeService;
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        return authService.login(request);
    }

    @Transactional
    public AppointmentResponse createAppointment(BusinessAppointmentRequest request) {
        currentUserService.requireUser(request.getCurrentUserId());
        return appointmentManagerService.create(toAppointmentRequest(request));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> checkEmployeeAvailability(BusinessAppointmentRequest request) {
        currentUserService.requireUser(request.getCurrentUserId());
        if (request.getEmployeeId() == null) {
            throw new BadRequestException("Vui lòng chọn nhân viên cần kiểm tra lịch làm việc");
        }
        Employee employee = employeeService.getEmployee(request.getEmployeeId());
        int totalDuration = request.getServiceIds().stream()
                .distinct()
                .map(spaService::getService)
                .mapToInt(com.group8.spabooking.entity.Service::getDurationMinutes)
                .sum();
        LocalTime endTime = request.getStartTime().plusMinutes(totalDuration);

        boolean hasWorkingSchedule = employeeScheduleRepository
                .findByEmployeeIdAndWorkDate(employee.getId(), request.getAppointmentDate())
                .stream()
                .anyMatch(schedule ->
                        !schedule.getStartTime().isAfter(request.getStartTime())
                                && !schedule.getEndTime().isBefore(endTime));
        if (!hasWorkingSchedule) {
            return Map.of(
                    "available", false,
                    "message", "Nhân viên không có ca làm phù hợp với thời gian lịch hẹn",
                    "endTime", endTime);
        }

        boolean hasOverlappingAppointment = appointmentRepository
                .findByEmployeeIdAndAppointmentDate(employee.getId(), request.getAppointmentDate())
                .stream()
                .filter(this::isActiveAppointmentForBooking)
                .anyMatch(existing ->
                        request.getStartTime().isBefore(existing.getEndTime())
                                && endTime.isAfter(existing.getStartTime()));
        if (hasOverlappingAppointment) {
            return Map.of(
                    "available", false,
                    "message", "Nhân viên đã có lịch hẹn bị trùng thời gian",
                    "endTime", endTime);
        }

        return Map.of(
                "available", true,
                "message", "Nhân viên có thể nhận lịch hẹn",
                "endTime", endTime);
    }

    @Transactional
    public AppointmentResponse assignEmployee(Long appointmentId, Long currentUserId, Long employeeId) {
        return appointmentManagerService.assignEmployee(appointmentId, currentUserId, employeeId);
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, Long currentUserId, String status) {
        return appointmentManagerService.updateStatus(appointmentId, currentUserId, status);
    }

    @Transactional
    public InvoiceResponse createInvoice(BusinessInvoiceRequest request) {
        currentUserService.requireAnyRole(request.getCurrentUserId(), "ADMIN", "EMPLOYEE");
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setAppointmentId(request.getAppointmentId());
        invoiceRequest.setDiscountAmount(request.getDiscountAmount());
        invoiceRequest.setPaymentMethod(request.getPaymentMethod());
        return invoiceService.create(invoiceRequest);
    }

    @Transactional
    public InvoiceResponse payInvoice(Long invoiceId, BusinessInvoicePaymentRequest request) {
        return invoiceService.pay(invoiceId, request.getCurrentUserId(), request.getPaymentMethod());
    }

    @Transactional
    public InvoiceResponse updatePaymentStatus(Long invoiceId, BusinessPaymentStatusRequest request) {
        return invoiceService.updateStatus(invoiceId, request.getCurrentUserId(), request.getPaymentStatus());
    }

    @Transactional
    public SmsReminderResponse generateSmsReminder(BusinessSmsReminderRequest request) {
        SmsReminderRequest smsRequest = new SmsReminderRequest();
        smsRequest.setAppointmentId(request.getAppointmentId());
        smsRequest.setPhone(request.getPhone());
        smsRequest.setMessage(request.getMessage());
        return smsReminderService.create(request.getCurrentUserId(), smsRequest);
    }

    private AppointmentRequest toAppointmentRequest(BusinessAppointmentRequest request) {
        AppointmentRequest appointmentRequest = new AppointmentRequest();
        appointmentRequest.setCustomerId(request.getCustomerId());
        appointmentRequest.setEmployeeId(request.getEmployeeId());
        appointmentRequest.setAppointmentDate(request.getAppointmentDate());
        appointmentRequest.setStartTime(request.getStartTime());
        appointmentRequest.setServiceIds(request.getServiceIds());
        appointmentRequest.setNote(request.getNote());
        return appointmentRequest;
    }

    private boolean isActiveAppointmentForBooking(Appointment appointment) {
        return !"Đã hủy".equals(appointment.getStatus())
                && !"Khách không đến".equals(appointment.getStatus());
    }
}
