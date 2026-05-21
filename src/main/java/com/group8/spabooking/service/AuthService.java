package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.LoginRequest;
import com.group8.spabooking.dto.request.RegisterRequest;
import com.group8.spabooking.dto.response.LoginResponse;
import com.group8.spabooking.dto.response.RegisterResponse;
import com.group8.spabooking.dto.response.UserResponse;
import com.group8.spabooking.entity.Customer;
import com.group8.spabooking.entity.Role;
import com.group8.spabooking.entity.User;
import com.group8.spabooking.exception.BadRequestException;
import com.group8.spabooking.repository.CustomerRepository;
import com.group8.spabooking.repository.RoleRepository;
import com.group8.spabooking.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordService passwordService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String login = request.getUsername().trim();
        User user = userRepository.findByUsernameOrEmailOrPhone(login, login, login)
                .filter(existing -> Boolean.TRUE.equals(existing.getActive()))
                .filter(existing -> passwordService.matches(normalizePassword(request.getPassword()), existing.getPassword()))
                .orElseThrow(() -> new BadRequestException("Sai username hoặc password"));

        return LoginResponse.builder()
                .message("Đăng nhập thành công")
                .user(UserResponse.from(user))
                .build();
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username đã tồn tại");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("CUSTOMER role is missing"));
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .username(username)
                .password(passwordService.encode(normalizePassword(request.getPassword())))
                .fullName(request.getFullName().trim())
                .email(trimOrNull(request.getEmail()))
                .phone(trimOrNull(request.getPhone()))
                .role(customerRole)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userRepository.save(user);
        Customer customer = Customer.builder()
                .user(savedUser)
                .fullName(savedUser.getFullName())
                .phone(savedUser.getPhone())
                .email(savedUser.getEmail())
                .gender(trimOrNull(request.getGender()))
                .createdAt(now)
                .updatedAt(now)
                .build();
        customerRepository.save(customer);

        return RegisterResponse.builder()
                .message("Đăng ký tài khoản thành công")
                .user(UserResponse.from(savedUser))
                .build();
    }

    private String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizePassword(String value) {
        return value == null ? "" : value.trim();
    }
}
