package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.EmployeeRequest;
import com.group8.spabooking.dto.response.EmployeeResponse;
import com.group8.spabooking.entity.Employee;
import com.group8.spabooking.entity.User;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.EmployeeRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        return EmployeeResponse.from(getEmployee(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        User user = userService.getUser(request.getUserId());
        LocalDateTime now = LocalDateTime.now();
        Employee employee = Employee.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .position(request.getPosition())
                .skillNote(request.getSkillNote())
                .active(activeOrDefault(request.getActive()))
                .createdAt(now)
                .updatedAt(now)
                .build();

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = getEmployee(id);
        employee.setUser(userService.getUser(request.getUserId()));
        employee.setFullName(request.getFullName());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setPosition(request.getPosition());
        employee.setSkillNote(request.getSkillNote());
        employee.setActive(activeOrDefault(request.getActive()));
        employee.setUpdatedAt(LocalDateTime.now());

        return EmployeeResponse.from(employee);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = getEmployee(id);
        employeeRepository.delete(employee);
    }

    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
    }

    private Boolean activeOrDefault(Boolean active) {
        return active == null || active;
    }
}
