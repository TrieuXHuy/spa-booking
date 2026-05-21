package com.example.spabooking.client.dto;

public record SmsReminderStatusRequestDto(
        String status,
        String errorMessage
) {
}
