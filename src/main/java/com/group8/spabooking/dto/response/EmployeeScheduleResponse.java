package com.group8.spabooking.dto.response;

import com.group8.spabooking.entity.EmployeeSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeScheduleResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;

    public static EmployeeScheduleResponse from(EmployeeSchedule schedule) {
        return EmployeeScheduleResponse.builder()
                .id(schedule.getId())
                .employeeId(schedule.getEmployee().getId())
                .employeeName(schedule.getEmployee().getFullName())
                .workDate(schedule.getWorkDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .note(schedule.getNote())
                .build();
    }
}
