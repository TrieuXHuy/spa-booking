package com.example.spabooking.client.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        RoleDto role
) {

    public String roleName() {
        return role == null ? "" : role.name();
    }
}
