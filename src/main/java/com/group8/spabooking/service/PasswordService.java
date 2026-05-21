package com.group8.spabooking.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    public String encode(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Không thể mã hóa mật khẩu", exception);
        }
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return matchesBcrypt(rawPassword, encodedPassword)
                || encode(rawPassword).equals(encodedPassword)
                || rawPassword.equals(encodedPassword);
    }

    private boolean matchesBcrypt(String rawPassword, String encodedPassword) {
        if (!encodedPassword.startsWith("$2a$")
                && !encodedPassword.startsWith("$2b$")
                && !encodedPassword.startsWith("$2y$")) {
            return false;
        }
        return BCRYPT.matches(rawPassword, encodedPassword);
    }
}
