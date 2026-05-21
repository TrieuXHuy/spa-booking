package com.group8.spabooking.dto.response;

import com.group8.spabooking.entity.Employee;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeResponse {

    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String position;
    private String skillNote;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EmployeeResponse from(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .userId(employee.getUser().getId())
                .username(employee.getUser().getUsername())
                .fullName(employee.getFullName())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .skillNote(employee.getSkillNote())
                .active(employee.getActive())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
