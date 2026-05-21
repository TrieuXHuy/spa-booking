package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsReminderRequest {

    @NotNull
    private Long appointmentId;

    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 1000)
    private String message;
}
