package com.example.spabooking.session;

import com.example.spabooking.client.dto.LoginResponse;
import com.example.spabooking.model.UserSession;

public final class SessionManager {

    private static Long currentUserId;
    private static String username;
    private static String fullName;
    private static String role;

    private SessionManager() {
    }

    public static void login(LoginResponse response) {
        if (response == null || response.user() == null) {
            clear();
            return;
        }
        currentUserId = response.userId();
        username = response.username();
        fullName = response.fullName();
        role = normalizeRole(response.role());
    }

    public static void clear() {
        currentUserId = null;
        username = null;
        fullName = null;
        role = null;
    }

    public static boolean isLoggedIn() {
        return currentUserId != null;
    }

    public static Long getCurrentUserId() {
        return currentUserId;
    }

    public static String getUsername() {
        return username;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getRole() {
        return role;
    }

    public static UserSession toUserSession() {
        return new UserSession(currentUserId, username, fullName, null, null, role);
    }

    private static String normalizeRole(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase();
    }
}
