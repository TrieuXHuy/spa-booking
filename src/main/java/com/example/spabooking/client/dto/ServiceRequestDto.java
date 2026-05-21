package com.example.spabooking.client.dto;

import java.math.BigDecimal;

public record ServiceRequestDto(
        String name,
        String description,
        BigDecimal price,
        Integer durationMinutes,
        Boolean active
) {
}
