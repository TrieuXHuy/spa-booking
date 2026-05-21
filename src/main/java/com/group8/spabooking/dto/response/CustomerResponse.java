package com.group8.spabooking.dto.response;

import com.group8.spabooking.entity.Customer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerResponse {

    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .userId(customer.getUser() == null ? null : customer.getUser().getId())
                .username(customer.getUser() == null ? null : customer.getUser().getUsername())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .gender(customer.getGender())
                .dateOfBirth(customer.getDateOfBirth())
                .note(customer.getNote())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
