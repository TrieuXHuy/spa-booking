package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentAssignEmployeeRequest {

    @NotNull
    private Long employeeId;
}
