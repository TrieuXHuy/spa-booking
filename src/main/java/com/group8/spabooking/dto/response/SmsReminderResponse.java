package com.group8.spabooking.dto.response;

import com.group8.spabooking.entity.SmsReminder;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SmsReminderResponse {

    private Long id;
    private Long appointmentId;
    private String customerName;
    private String phone;
    private String message;
    private String status;
    private LocalDateTime sentAt;
    private String errorMessage;
    private LocalDateTime createdAt;

    public static SmsReminderResponse from(SmsReminder smsReminder) {
        return SmsReminderResponse.builder()
                .id(smsReminder.getId())
                .appointmentId(smsReminder.getAppointment().getId())
                .customerName(smsReminder.getAppointment().getCustomer().getFullName())
                .phone(smsReminder.getPhone())
                .message(smsReminder.getMessage())
                .status(smsReminder.getStatus())
                .sentAt(smsReminder.getSentAt())
                .errorMessage(smsReminder.getErrorMessage())
                .createdAt(smsReminder.getCreatedAt())
                .build();
    }
}
