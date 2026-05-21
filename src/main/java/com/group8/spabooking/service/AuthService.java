package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.LoginRequest;
import com.group8.spabooking.dto.response.LoginResponse;
import com.group8.spabooking.dto.response.UserResponse;
import com.group8.spabooking.entity.User;
import com.group8.spabooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername().trim())
                .filter(existing -> Boolean.TRUE.equals(existing.getActive()))
                .filter(existing -> passwordEncoder.matches(request.getPassword(), existing.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        return LoginResponse.builder()
                .message("Đăng nhập thành công")
                .user(UserResponse.from(user))
                .build();
    }
}
