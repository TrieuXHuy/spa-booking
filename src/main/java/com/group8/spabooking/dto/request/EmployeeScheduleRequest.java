package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeScheduleRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate workDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @Size(max = 500)
    private String note;
}
