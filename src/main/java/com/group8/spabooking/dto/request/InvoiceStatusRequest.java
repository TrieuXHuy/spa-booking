package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceStatusRequest {

    @NotBlank
    @Size(max = 50)
    private String paymentStatus;
}
