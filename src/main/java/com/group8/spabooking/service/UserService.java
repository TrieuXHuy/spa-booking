package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.UserRequest;
import com.group8.spabooking.dto.request.UserUpdateRequest;
import com.group8.spabooking.dto.response.UserResponse;
import com.group8.spabooking.entity.Role;
import com.group8.spabooking.entity.User;
import com.group8.spabooking.exception.BadRequestException;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(getUser(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        String username = request.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username đã tồn tại");
        }

        Role role = roleService.getRole(request.getRoleId());
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(role)
                .active(activeOrDefault(request.getActive()))
                .createdAt(now)
                .updatedAt(now)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = getUser(id);
        Role role = roleService.getRole(request.getRoleId());

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setActive(activeOrDefault(request.getActive()));
        user.setUpdatedAt(LocalDateTime.now());

        return UserResponse.from(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
    }

    private Boolean activeOrDefault(Boolean active) {
        return active == null || active;
    }
}
