package com.example.spabooking.client.dto;

public record SmsReminderRequestDto(
        Long appointmentId,
        String phone,
        String message
) {
}
