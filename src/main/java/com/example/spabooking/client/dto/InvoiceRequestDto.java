package com.example.spabooking.client.dto;

import java.math.BigDecimal;

public record InvoiceRequestDto(
        Long appointmentId,
        BigDecimal discountAmount,
        String paymentMethod
) {
}
