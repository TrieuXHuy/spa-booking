package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessAssignEmployeeRequest {

    @NotNull
    private Long currentUserId;

    @NotNull
    private Long employeeId;
}
