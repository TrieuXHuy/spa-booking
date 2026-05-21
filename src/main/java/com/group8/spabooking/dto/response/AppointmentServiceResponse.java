package com.group8.spabooking.dto.response;

import com.group8.spabooking.entity.AppointmentService;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointmentServiceResponse {

    private Long id;
    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
    private Integer durationMinutes;

    public static AppointmentServiceResponse from(AppointmentService appointmentService) {
        return AppointmentServiceResponse.builder()
                .id(appointmentService.getId())
                .serviceId(appointmentService.getService().getId())
                .serviceName(appointmentService.getService().getName())
                .price(appointmentService.getPrice())
                .durationMinutes(appointmentService.getDurationMinutes())
                .build();
    }
}
