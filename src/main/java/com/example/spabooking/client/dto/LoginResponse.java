package com.example.spabooking.client.dto;

public record LoginResponse(String message, UserDto user) {

    public Long userId() {
        return user == null ? null : user.id();
    }

    public String username() {
        return user == null ? null : user.username();
    }

    public String fullName() {
        return user == null ? null : user.fullName();
    }

    public String role() {
        return user == null || user.role() == null ? null : user.role().name();
    }

    public record UserDto(
            Long id,
            String username,
            String fullName,
            String email,
            String phone,
            Boolean active,
            RoleDto role
    ) {
    }

    public record RoleDto(Long id, String name, String description) {
    }
}
