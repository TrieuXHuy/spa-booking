package com.example.spabooking.client.dto;

import java.time.LocalDateTime;

public record EmployeeDto(
        Long id,
        Long userId,
        String username,
        String fullName,
        String phone,
        String email,
        String position,
        String skillNote,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
