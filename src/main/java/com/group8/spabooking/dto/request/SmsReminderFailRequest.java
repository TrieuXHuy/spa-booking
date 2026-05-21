package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsReminderFailRequest {

    @NotBlank
    @Size(max = 500)
    private String errorMessage;
}
