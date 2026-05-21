package com.example.spabooking.client.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record EmployeeScheduleDto(
        Long id,
        Long employeeId,
        String employeeName,
        LocalDate workDate,
        LocalTime startTime,
        LocalTime endTime,
        String note
) {
}
