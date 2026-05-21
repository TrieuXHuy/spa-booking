package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    private Long userId;

    @NotBlank
    @Size(max = 150)
    private String fullName;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String gender;

    private LocalDate dateOfBirth;

    @Size(max = 500)
    private String note;
}
