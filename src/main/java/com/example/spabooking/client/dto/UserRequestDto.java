package com.example.spabooking.client.dto;

public record UserRequestDto(
        String username,
        String password,
        String fullName,
        String email,
        String phone,
        Long roleId,
        Boolean active
) {
}
