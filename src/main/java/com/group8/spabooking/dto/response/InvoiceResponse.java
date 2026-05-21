package com.group8.spabooking.dto.response;

import com.group8.spabooking.entity.Invoice;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceResponse {

    private Long id;
    private Long appointmentId;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public static InvoiceResponse from(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .appointmentId(invoice.getAppointment().getId())
                .customerName(invoice.getAppointment().getCustomer().getFullName())
                .totalAmount(invoice.getTotalAmount())
                .discountAmount(invoice.getDiscountAmount())
                .finalAmount(resolveFinalAmount(invoice))
                .paymentMethod(invoice.getPaymentMethod())
                .paymentStatus(invoice.getPaymentStatus())
                .paidAt(invoice.getPaidAt())
                .createdAt(invoice.getCreatedAt())
                .build();
    }

    private static BigDecimal resolveFinalAmount(Invoice invoice) {
        if (invoice.getFinalAmount() != null) {
            return invoice.getFinalAmount();
        }
        BigDecimal discountAmount = invoice.getDiscountAmount() == null
                ? BigDecimal.ZERO
                : invoice.getDiscountAmount();
        return invoice.getTotalAmount().subtract(discountAmount);
    }
}
