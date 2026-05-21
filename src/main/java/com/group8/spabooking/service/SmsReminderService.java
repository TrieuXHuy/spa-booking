package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.SmsReminderFailRequest;
import com.group8.spabooking.dto.request.SmsReminderRequest;
import com.group8.spabooking.dto.request.SmsReminderStatusRequest;
import com.group8.spabooking.dto.response.SmsReminderResponse;
import com.group8.spabooking.entity.Appointment;
import com.group8.spabooking.entity.SmsReminder;
import com.group8.spabooking.exception.BadRequestException;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.SmsReminderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SmsReminderService {

    private static final String PENDING_STATUS = "Chưa gửi";
    private static final String SENT_STATUS = "Đã gửi";
    private static final String FAILED_STATUS = "Gửi lỗi";
    private static final Set<String> VALID_STATUSES = Set.of(PENDING_STATUS, SENT_STATUS, FAILED_STATUS);

    private final SmsReminderRepository smsReminderRepository;
    private final AppointmentManagerService appointmentManagerService;

    @Transactional(readOnly = true)
    public List<SmsReminderResponse> findAll(Long appointmentId, String status) {
        return findReminders(appointmentId, status).stream()
                .map(SmsReminderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SmsReminderResponse findById(Long id) {
        return SmsReminderResponse.from(getSmsReminder(id));
    }

    @Transactional
    public SmsReminderResponse create(SmsReminderRequest request) {
        Appointment appointment = appointmentManagerService.getAppointment(request.getAppointmentId());
        SmsReminder smsReminder = SmsReminder.builder()
                .appointment(appointment)
                .phone(resolvePhone(request.getPhone(), appointment))
                .message(request.getMessage())
                .status(PENDING_STATUS)
                .createdAt(LocalDateTime.now())
                .build();

        return SmsReminderResponse.from(smsReminderRepository.save(smsReminder));
    }

    @Transactional
    public SmsReminderResponse markSent(Long id) {
        SmsReminder smsReminder = getSmsReminder(id);
        smsReminder.setStatus(SENT_STATUS);
        smsReminder.setSentAt(LocalDateTime.now());
        smsReminder.setErrorMessage(null);
        return SmsReminderResponse.from(smsReminder);
    }

    @Transactional
    public SmsReminderResponse markFailed(Long id, SmsReminderFailRequest request) {
        SmsReminder smsReminder = getSmsReminder(id);
        smsReminder.setStatus(FAILED_STATUS);
        smsReminder.setSentAt(null);
        smsReminder.setErrorMessage(request.getErrorMessage());
        return SmsReminderResponse.from(smsReminder);
    }

    @Transactional
    public SmsReminderResponse updateStatus(Long id, SmsReminderStatusRequest request) {
        String status = request.getStatus().trim();
        if (!VALID_STATUSES.contains(status)) {
            throw new BadRequestException("Trạng thái SMS không hợp lệ");
        }

        SmsReminder smsReminder = getSmsReminder(id);
        smsReminder.setStatus(status);
        if (SENT_STATUS.equals(status)) {
            smsReminder.setSentAt(LocalDateTime.now());
            smsReminder.setErrorMessage(null);
        } else if (FAILED_STATUS.equals(status)) {
            smsReminder.setSentAt(null);
            smsReminder.setErrorMessage(request.getErrorMessage());
        } else {
            smsReminder.setSentAt(null);
            smsReminder.setErrorMessage(null);
        }
        return SmsReminderResponse.from(smsReminder);
    }

    @Transactional
    public void delete(Long id) {
        SmsReminder smsReminder = getSmsReminder(id);
        smsReminderRepository.delete(smsReminder);
    }

    public SmsReminder getSmsReminder(Long id) {
        return smsReminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy SMS nhắc lịch"));
    }

    private List<SmsReminder> findReminders(Long appointmentId, String status) {
        if (appointmentId != null) {
            return smsReminderRepository.findByAppointmentId(appointmentId);
        }
        if (status != null && !status.isBlank()) {
            return smsReminderRepository.findByStatus(status.trim());
        }
        return smsReminderRepository.findAll();
    }

    private String resolvePhone(String phone, Appointment appointment) {
        if (phone != null && !phone.isBlank()) {
            return phone.trim();
        }
        return appointment.getCustomer().getPhone();
    }
}
