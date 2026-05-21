package com.example.spabooking.client.dto;

public record UserUpdateRequestDto(
        String fullName,
        String email,
        String phone,
        Long roleId,
        Boolean active
) {
}
