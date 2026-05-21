package com.example.spabooking.client.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record EmployeeScheduleRequestDto(
        Long employeeId,
        LocalDate workDate,
        LocalTime startTime,
        LocalTime endTime,
        String note
) {
}
