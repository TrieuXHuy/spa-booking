package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessInvoicePaymentRequest {

    @NotNull
    private Long currentUserId;

    @NotBlank
    @Size(max = 50)
    private String paymentMethod;
}
