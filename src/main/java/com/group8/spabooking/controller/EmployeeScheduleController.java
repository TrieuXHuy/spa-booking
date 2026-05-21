package com.group8.spabooking.controller;

import com.group8.spabooking.dto.request.EmployeeScheduleRequest;
import com.group8.spabooking.dto.response.EmployeeScheduleResponse;
import com.group8.spabooking.service.EmployeeScheduleService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee-schedules")
@RequiredArgsConstructor
public class EmployeeScheduleController {

    private final EmployeeScheduleService employeeScheduleService;

    @GetMapping
    public List<EmployeeScheduleResponse> findAll(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate) {
        return employeeScheduleService.findAll(employeeId, workDate);
    }

    @GetMapping("/{id}")
    public EmployeeScheduleResponse findById(@PathVariable Long id) {
        return employeeScheduleService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeScheduleResponse create(@Valid @RequestBody EmployeeScheduleRequest request) {
        return employeeScheduleService.create(request);
    }

    @PutMapping("/{id}")
    public EmployeeScheduleResponse update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeScheduleRequest request) {
        return employeeScheduleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeScheduleService.delete(id);
    }
}
