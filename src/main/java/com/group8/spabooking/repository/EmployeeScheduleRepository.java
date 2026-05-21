package com.group8.spabooking.repository;

import com.group8.spabooking.entity.EmployeeSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeScheduleRepository extends JpaRepository<EmployeeSchedule, Long> {

    List<EmployeeSchedule> findByEmployeeId(Long employeeId);

    List<EmployeeSchedule> findByWorkDate(LocalDate workDate);

    List<EmployeeSchedule> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
}
