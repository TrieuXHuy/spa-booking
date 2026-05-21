package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceRequest {

    @NotNull
    private Long appointmentId;

    @DecimalMin(value = "0.0")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Size(max = 50)
    private String paymentMethod;
}
