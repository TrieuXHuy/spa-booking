package com.example.spabooking.client.dto;

public record RegisterRequest(
        String fullName,
        String username,
        String password,
        String email,
        String phone,
        String gender
) {
}
