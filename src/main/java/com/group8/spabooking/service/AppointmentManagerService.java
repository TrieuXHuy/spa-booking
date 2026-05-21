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
import com.group8.spabooking.repository.EmployeeScheduleRepository;
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
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final CurrentUserService currentUserService;

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

        validateEmployeeAvailability(null, employee, request.getAppointmentDate(), appointment.getStartTime(), appointment.getEndTime());
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

        validateEmployeeAvailability(
                appointment.getId(),
                appointment.getEmployee(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime());
        appointmentServiceRepository.deleteByAppointmentId(appointment.getId());
        saveAppointmentServices(appointment, selectedServices);
        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse assignEmployee(Long id, AppointmentAssignEmployeeRequest request) {
        Appointment appointment = getAppointment(id);
        Employee employee = employeeService.getEmployee(request.getEmployeeId());
        validateEmployeeAvailability(
                appointment.getId(),
                employee,
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime());
        appointment.setEmployee(employee);
        appointment.setUpdatedAt(LocalDateTime.now());
        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse assignEmployee(Long id, Long currentUserId, Long employeeId) {
        currentUserService.requireAnyRole(currentUserId, "ADMIN", "EMPLOYEE");
        AppointmentAssignEmployeeRequest request = new AppointmentAssignEmployeeRequest();
        request.setEmployeeId(employeeId);
        return assignEmployee(id, request);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatusRequest request) {
        String status = request.getStatus().trim();
        if (!VALID_STATUSES.contains(status)) {
            throw new BadRequestException("Trạng thái lịch hẹn không hợp lệ");
        }

        Appointment appointment = getAppointment(id);
        validateStatusTransition(appointment.getStatus(), status);
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        return toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, Long currentUserId, String status) {
        String normalizedStatus = status.trim();
        if ("Đã xác nhận".equals(normalizedStatus)) {
            currentUserService.requireAnyRole(currentUserId, "ADMIN", "EMPLOYEE");
        }
        AppointmentStatusRequest request = new AppointmentStatusRequest();
        request.setStatus(normalizedStatus);
        return updateStatus(id, request);
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

    private void validateEmployeeAvailability(
            Long currentAppointmentId,
            Employee employee,
            LocalDate appointmentDate,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime) {
        if (employee == null) {
            return;
        }

        boolean hasWorkingSchedule = employeeScheduleRepository
                .findByEmployeeIdAndWorkDate(employee.getId(), appointmentDate)
                .stream()
                .anyMatch(schedule ->
                        !schedule.getStartTime().isAfter(startTime)
                                && !schedule.getEndTime().isBefore(endTime));
        if (!hasWorkingSchedule) {
            throw new BadRequestException("Nhân viên không có ca làm phù hợp với thời gian lịch hẹn");
        }

        boolean hasOverlappingAppointment = appointmentRepository
                .findByEmployeeIdAndAppointmentDate(employee.getId(), appointmentDate)
                .stream()
                .filter(existing -> currentAppointmentId == null || !existing.getId().equals(currentAppointmentId))
                .filter(existing -> !"Đã hủy".equals(existing.getStatus()))
                .filter(existing -> !"Khách không đến".equals(existing.getStatus()))
                .anyMatch(existing -> startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime()));
        if (hasOverlappingAppointment) {
            throw new BadRequestException("Nhân viên đã có lịch hẹn bị trùng thời gian");
        }
    }

    private void validateStatusTransition(String currentStatus, String nextStatus) {
        if (currentStatus.equals(nextStatus)) {
            return;
        }
        boolean validTransition = switch (currentStatus) {
            case "Chờ xác nhận" -> Set.of("Đã xác nhận", "Đã hủy").contains(nextStatus);
            case "Đã xác nhận" -> Set.of("Đang thực hiện", "Đã hủy", "Khách không đến").contains(nextStatus);
            case "Đang thực hiện" -> Set.of("Hoàn thành", "Đã hủy").contains(nextStatus);
            case "Hoàn thành", "Đã hủy", "Khách không đến" -> false;
            default -> false;
        };
        if (!validTransition) {
            throw new BadRequestException("Không thể chuyển trạng thái lịch hẹn từ "
                    + currentStatus + " sang " + nextStatus);
        }
    }
}
