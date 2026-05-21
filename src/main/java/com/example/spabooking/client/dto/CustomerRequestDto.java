package com.example.spabooking.client.dto;

import java.time.LocalDate;

public record CustomerRequestDto(
        Long userId,
        String fullName,
        String phone,
        String email,
        String gender,
        LocalDate dateOfBirth,
        String note
) {
}
