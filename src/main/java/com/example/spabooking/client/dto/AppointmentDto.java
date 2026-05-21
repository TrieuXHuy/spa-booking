package com.example.spabooking.client.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record AppointmentDto(
        Long id,
        Long customerId,
        String customerName,
        Long employeeId,
        String employeeName,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AppointmentServiceDto> services
) {
}
