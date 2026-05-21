package com.example.spabooking.client.dto;

import java.math.BigDecimal;

public record AppointmentServiceDto(
        Long id,
        Long serviceId,
        String serviceName,
        BigDecimal price,
        Integer durationMinutes
) {
}
