package com.example.spabooking.client.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerDto(
        Long id,
        Long userId,
        String username,
        String fullName,
        String phone,
        String email,
        String gender,
        LocalDate dateOfBirth,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
