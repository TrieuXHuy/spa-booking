package com.example.spabooking.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceDto(
        Long id,
        Long appointmentId,
        String customerName,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        String paymentMethod,
        String paymentStatus,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
}
