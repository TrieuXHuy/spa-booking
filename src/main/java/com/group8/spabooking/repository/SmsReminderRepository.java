package com.group8.spabooking.repository;

import com.group8.spabooking.entity.SmsReminder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsReminderRepository extends JpaRepository<SmsReminder, Long> {

    List<SmsReminder> findByAppointmentId(Long appointmentId);

    List<SmsReminder> findByStatus(String status);
}
