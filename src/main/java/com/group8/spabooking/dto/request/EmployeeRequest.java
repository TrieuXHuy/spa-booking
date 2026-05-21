package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequest {

    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 150)
    private String fullName;

    @Size(max = 20)
    private String phone;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 100)
    private String position;

    @Size(max = 500)
    private String skillNote;

    private Boolean active = true;
}
