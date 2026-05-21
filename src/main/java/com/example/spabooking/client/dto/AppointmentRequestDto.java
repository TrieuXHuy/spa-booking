package com.example.spabooking.client.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AppointmentRequestDto(
        Long customerId,
        Long employeeId,
        LocalDate appointmentDate,
        LocalTime startTime,
        List<Long> serviceIds,
        String note
) {
}
