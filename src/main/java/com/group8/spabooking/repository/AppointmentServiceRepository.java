package com.group8.spabooking.repository;

import com.group8.spabooking.entity.AppointmentService;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {

    List<AppointmentService> findByAppointmentId(Long appointmentId);

    void deleteByAppointmentId(Long appointmentId);
}
