package com.group8.spabooking.config;

import com.group8.spabooking.entity.Role;
import com.group8.spabooking.entity.User;
import com.group8.spabooking.repository.RoleRepository;
import com.group8.spabooking.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.group8.spabooking.service.PasswordService;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdmin();
    }

    private void seedRoles() {
        List<Role> roles = List.of(
                Role.builder().name("ADMIN").description("Quản trị hệ thống / chủ cửa hàng").build(),
                Role.builder().name("EMPLOYEE").description("Nhân viên spa/salon").build(),
                Role.builder().name("CUSTOMER").description("Khách hàng").build());

        roles.stream()
                .filter(role -> !roleRepository.existsByName(role.getName()))
                .forEach(roleRepository::save);
    }

    private void seedAdmin() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role is missing"));
        LocalDateTime now = LocalDateTime.now();
        userRepository.findByUsername("admin").ifPresentOrElse(existingAdmin -> {
            existingAdmin.setPassword(passwordService.encode("admin123"));
            existingAdmin.setFullName("System Admin");
            existingAdmin.setRole(adminRole);
            existingAdmin.setActive(true);
            existingAdmin.setUpdatedAt(now);
        }, () -> {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordService.encode("admin123"))
                    .fullName("System Admin")
                    .role(adminRole)
                    .active(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            userRepository.save(admin);
        });
    }
}
