package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.EmployeeScheduleRequest;
import com.group8.spabooking.dto.response.EmployeeScheduleResponse;
import com.group8.spabooking.entity.Employee;
import com.group8.spabooking.entity.EmployeeSchedule;
import com.group8.spabooking.exception.BadRequestException;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.EmployeeScheduleRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeScheduleService {

    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final EmployeeService employeeService;

    @Transactional(readOnly = true)
    public List<EmployeeScheduleResponse> findAll(Long employeeId, LocalDate workDate) {
        return findSchedules(employeeId, workDate).stream()
                .map(EmployeeScheduleResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeScheduleResponse findById(Long id) {
        return EmployeeScheduleResponse.from(getEmployeeSchedule(id));
    }

    @Transactional
    public EmployeeScheduleResponse create(EmployeeScheduleRequest request) {
        validateTimeRange(request);
        Employee employee = employeeService.getEmployee(request.getEmployeeId());
        EmployeeSchedule schedule = EmployeeSchedule.builder()
                .employee(employee)
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .note(request.getNote())
                .build();

        return EmployeeScheduleResponse.from(employeeScheduleRepository.save(schedule));
    }

    @Transactional
    public EmployeeScheduleResponse update(Long id, EmployeeScheduleRequest request) {
        validateTimeRange(request);
        EmployeeSchedule schedule = getEmployeeSchedule(id);
        schedule.setEmployee(employeeService.getEmployee(request.getEmployeeId()));
        schedule.setWorkDate(request.getWorkDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setNote(request.getNote());

        return EmployeeScheduleResponse.from(schedule);
    }

    @Transactional
    public void delete(Long id) {
        EmployeeSchedule schedule = getEmployeeSchedule(id);
        employeeScheduleRepository.delete(schedule);
    }

    public EmployeeSchedule getEmployeeSchedule(Long id) {
        return employeeScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch làm việc"));
    }

    private List<EmployeeSchedule> findSchedules(Long employeeId, LocalDate workDate) {
        if (employeeId != null && workDate != null) {
            return employeeScheduleRepository.findByEmployeeIdAndWorkDate(employeeId, workDate);
        }
        if (employeeId != null) {
            return employeeScheduleRepository.findByEmployeeId(employeeId);
        }
        if (workDate != null) {
            return employeeScheduleRepository.findByWorkDate(workDate);
        }
        return employeeScheduleRepository.findAll();
    }

    private void validateTimeRange(EmployeeScheduleRequest request) {
        if (request.getStartTime() != null
                && request.getEndTime() != null
                && !request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("Giờ kết thúc phải sau giờ bắt đầu");
        }
    }
}
