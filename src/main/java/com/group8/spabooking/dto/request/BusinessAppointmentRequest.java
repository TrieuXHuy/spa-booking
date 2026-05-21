package com.group8.spabooking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessAppointmentRequest {

    @NotNull
    private Long currentUserId;

    @NotNull
    private Long customerId;

    private Long employeeId;

    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime startTime;

    @NotEmpty
    private List<Long> serviceIds;

    @Size(max = 500)
    private String note;
}
