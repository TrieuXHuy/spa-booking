package com.group8.spabooking.repository;

import com.group8.spabooking.entity.Invoice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);
}
