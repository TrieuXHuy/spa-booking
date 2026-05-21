package com.example.spabooking.client.dto;

public record EmployeeRequestDto(
        Long userId,
        String fullName,
        String phone,
        String email,
        String position,
        String skillNote,
        Boolean active
) {
}
