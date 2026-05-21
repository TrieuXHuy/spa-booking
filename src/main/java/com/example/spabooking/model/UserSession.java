package com.example.spabooking.model;

public record UserSession(
        Long userId,
        String username,
        String fullName,
        String email,
        String phone,
        String roleName
) {

    public String displayName() {
        if (fullName != null && !fullName.isBlank()) {
            return fullName;
        }
        return username;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(roleName);
    }
}
