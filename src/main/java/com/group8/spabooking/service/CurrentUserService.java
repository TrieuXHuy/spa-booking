package com.group8.spabooking.service;

import com.group8.spabooking.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserService userService;

    public User requireUser(Long currentUserId) {
        if (currentUserId == null) {
            throw new com.group8.spabooking.exception.BadRequestException("Thiếu currentUserId");
        }
        User user = userService.getUser(currentUserId);
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new com.group8.spabooking.exception.BadRequestException("Tài khoản hiện tại đã bị khóa");
        }
        return user;
    }

    public void requireAnyRole(Long currentUserId, String... roles) {
        User user = requireUser(currentUserId);
        String currentRole = user.getRole().getName();
        for (String role : roles) {
            if (role.equals(currentRole)) {
                return;
            }
        }
        throw new com.group8.spabooking.exception.BadRequestException("Bạn không có quyền thực hiện thao tác này");
    }

    public boolean hasRole(Long currentUserId, String role) {
        User user = requireUser(currentUserId);
        return role.equals(user.getRole().getName());
    }
}
