package com.group8.spabooking.dto.response;

import com.group8.spabooking.entity.Appointment;
import com.group8.spabooking.entity.AppointmentService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointmentResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long employeeId;
    private String employeeName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AppointmentServiceResponse> services;

    public static AppointmentResponse from(
            Appointment appointment,
            List<AppointmentService> appointmentServices) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .customerId(appointment.getCustomer().getId())
                .customerName(appointment.getCustomer().getFullName())
                .employeeId(appointment.getEmployee() == null ? null : appointment.getEmployee().getId())
                .employeeName(appointment.getEmployee() == null ? null : appointment.getEmployee().getFullName())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .note(appointment.getNote())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .services(appointmentServices.stream()
                        .map(AppointmentServiceResponse::from)
                        .toList())
                .build();
    }
}
