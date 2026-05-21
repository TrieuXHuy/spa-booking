package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.AppointmentAssignEmployeeRequest;
import com.group8.spabooking.dto.request.AppointmentRequest;
import com.group8.spabooking.dto.request.AppointmentStatusRequest;
import com.group8.spabooking.dto.response.AppointmentResponse;
import com.group8.spabooking.entity.Appointment;
import com.group8.spabooking.entity.AppointmentService;
import com.group8.spabooking.entity.Customer;
import com.group8.spabooking.entity.Employee;
import com.group8.spabooking.exception.BadRequestException;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.AppointmentRepository;
import com.group8.spabooking.repository.AppointmentServiceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentManagerService {

    private static final String DEFAULT_STATUS = "Chờ xác nhận";
    private static final Set<String> VALID_STATUSES = Set.of(
            "Chờ xác nhận",
            "Đã xác nhận",
            "Đang thực hiện",
            "Hoàn thành",
            "Đã hủy",
            "Khách không đến");

    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final SpaService spaService;

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll(Long customerId, Long employeeId, LocalDate appointmentDate) {
        return findAppointments(customerId, employeeId, appointmentDate).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        return toResponse(getAppointment(id));
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Customer customer = customerService.getCustomer(request.getCustomerId());
        Employee employee = findEmployeeOrNull(request.getEmployeeId());
        List<com.group8.spabooking.entity.Service> selectedServices = findSelectedServices(request.getServiceIds());
        int totalDuration = totalDuration(selectedServices);
        LocalDateTime now = LocalDateTime.now();

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .employee(employee)
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(request.getStartTime().plusMinutes(totalDuration))
                .status(DEFAULT_STATUS)
                .note(request.getNote())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        saveAppointmentServices(savedAppointment, selectedServices);
        return toResponse(savedAppointment);
    }

    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = getAppointment(id);
        List<com.group8.spabooking.entity.Service> selectedServices = findSelectedServices(request.getServiceIds());
        int totalDuration = totalDuration(selectedServices);

        appointment.setCustomer(customerService.getCustomer(request.getCustomerId()));
        appointment.setEmployee(findEmployeeOrNull(request.getEmployeeId()));
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getStartTime().plusMinutes(totalDuration));
        appointment.setNote(request.getNote());
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentServiceRepository.deleteByAppointmentId(appointment.getId());
        saveAppointmentServices(appointment, selectedServices);
        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse assignEmployee(Long id, AppointmentAssignEmployeeRequest request) {
        Appointment appointment = getAppointment(id);
        appointment.setEmployee(employeeService.getEmployee(request.getEmployeeId()));
        appointment.setUpdatedAt(LocalDateTime.now());
        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatusRequest request) {
        String status = request.getStatus().trim();
        if (!VALID_STATUSES.contains(status)) {
            throw new BadRequestException("Trạng thái lịch hẹn không hợp lệ");
        }

        Appointment appointment = getAppointment(id);
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        return toResponse(appointment);
    }

    @Transactional
    public void delete(Long id) {
        Appointment appointment = getAppointment(id);
        appointmentServiceRepository.deleteByAppointmentId(id);
        appointmentRepository.delete(appointment);
    }

    public Appointment getAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.from(
                appointment,
                appointmentServiceRepository.findByAppointmentId(appointment.getId()));
    }

    private List<Appointment> findAppointments(Long customerId, Long employeeId, LocalDate appointmentDate) {
        if (customerId != null && appointmentDate != null) {
            return appointmentRepository.findByCustomerIdAndAppointmentDate(customerId, appointmentDate);
        }
        if (employeeId != null && appointmentDate != null) {
            return appointmentRepository.findByEmployeeIdAndAppointmentDate(employeeId, appointmentDate);
        }
        if (customerId != null) {
            return appointmentRepository.findByCustomerId(customerId);
        }
        if (employeeId != null) {
            return appointmentRepository.findByEmployeeId(employeeId);
        }
        if (appointmentDate != null) {
            return appointmentRepository.findByAppointmentDate(appointmentDate);
        }
        return appointmentRepository.findAll();
    }

    private Employee findEmployeeOrNull(Long employeeId) {
        return employeeId == null ? null : employeeService.getEmployee(employeeId);
    }

    private List<com.group8.spabooking.entity.Service> findSelectedServices(List<Long> serviceIds) {
        return serviceIds.stream()
                .distinct()
                .map(spaService::getService)
                .toList();
    }

    private int totalDuration(List<com.group8.spabooking.entity.Service> selectedServices) {
        return selectedServices.stream()
                .mapToInt(com.group8.spabooking.entity.Service::getDurationMinutes)
                .sum();
    }

    private void saveAppointmentServices(
            Appointment appointment,
            List<com.group8.spabooking.entity.Service> selectedServices) {
        List<AppointmentService> appointmentServices = selectedServices.stream()
                .map(service -> AppointmentService.builder()
                        .appointment(appointment)
                        .service(service)
                        .price(service.getPrice())
                        .durationMinutes(service.getDurationMinutes())
                        .build())
                .toList();
        appointmentServiceRepository.saveAll(appointmentServices);
    }
}
