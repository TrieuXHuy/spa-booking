package com.group8.spabooking.repository;

import com.group8.spabooking.entity.Appointment;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByCustomerId(Long customerId);

    List<Appointment> findByEmployeeId(Long employeeId);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findByCustomerIdAndAppointmentDate(Long customerId, LocalDate appointmentDate);

    List<Appointment> findByEmployeeIdAndAppointmentDate(Long employeeId, LocalDate appointmentDate);
}
