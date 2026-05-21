package com.example.spabooking.client.dto;

import java.time.LocalDateTime;

public record SmsReminderDto(
        Long id,
        Long appointmentId,
        String customerName,
        String phone,
        String message,
        String status,
        LocalDateTime sentAt,
        String errorMessage,
        LocalDateTime createdAt
) {
}
