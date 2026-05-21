package com.example.spabooking.client;

import com.example.spabooking.client.dto.AppointmentDto;
import com.example.spabooking.client.dto.AppointmentRequestDto;
import com.example.spabooking.client.dto.AppointmentStatusRequestDto;
import com.example.spabooking.client.dto.CustomerDto;
import com.example.spabooking.client.dto.CustomerRequestDto;
import com.example.spabooking.client.dto.EmployeeDto;
import com.example.spabooking.client.dto.EmployeeRequestDto;
import com.example.spabooking.client.dto.EmployeeScheduleDto;
import com.example.spabooking.client.dto.EmployeeScheduleRequestDto;
import com.example.spabooking.client.dto.InvoiceDto;
import com.example.spabooking.client.dto.InvoicePaymentRequestDto;
import com.example.spabooking.client.dto.InvoiceRequestDto;
import com.example.spabooking.client.dto.InvoiceStatusRequestDto;
import com.example.spabooking.client.dto.RoleDto;
import com.example.spabooking.client.dto.ServiceDto;
import com.example.spabooking.client.dto.ServiceRequestDto;
import com.example.spabooking.client.dto.SmsReminderDto;
import com.example.spabooking.client.dto.SmsReminderRequestDto;
import com.example.spabooking.client.dto.SmsReminderStatusRequestDto;
import com.example.spabooking.client.dto.UserDto;
import com.example.spabooking.client.dto.UserRequestDto;
import com.example.spabooking.client.dto.UserUpdateRequestDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdminClient {

    private final ApiClient apiClient;

    public AdminClient() {
        this(new ApiClient());
    }

    public AdminClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<List<UserDto>> findUsers() {
        return apiClient.get("/api/users", new TypeReference<>() {
        });
    }

    public CompletableFuture<List<RoleDto>> findRoles() {
        return apiClient.get("/api/roles", new TypeReference<>() {
        });
    }

    public CompletableFuture<UserDto> createUser(UserRequestDto request) {
        return apiClient.post("/api/users", request, UserDto.class);
    }

    public CompletableFuture<UserDto> updateUser(Long id, UserUpdateRequestDto request) {
        return apiClient.put("/api/users/" + id, request, UserDto.class);
    }

    public CompletableFuture<Void> deleteUser(Long id) {
        return apiClient.delete("/api/users/" + id);
    }

    public CompletableFuture<List<CustomerDto>> findCustomers() {
        return apiClient.get("/api/customers", new TypeReference<>() {
        });
    }

    public CompletableFuture<CustomerDto> createCustomer(CustomerRequestDto request) {
        return apiClient.post("/api/customers", request, CustomerDto.class);
    }

    public CompletableFuture<CustomerDto> updateCustomer(Long id, CustomerRequestDto request) {
        return apiClient.put("/api/customers/" + id, request, CustomerDto.class);
    }

    public CompletableFuture<Void> deleteCustomer(Long id) {
        return apiClient.delete("/api/customers/" + id);
    }

    public CompletableFuture<List<EmployeeDto>> findEmployees() {
        return apiClient.get("/api/employees", new TypeReference<>() {
        });
    }

    public CompletableFuture<EmployeeDto> createEmployee(EmployeeRequestDto request) {
        return apiClient.post("/api/employees", request, EmployeeDto.class);
    }

    public CompletableFuture<EmployeeDto> updateEmployee(Long id, EmployeeRequestDto request) {
        return apiClient.put("/api/employees/" + id, request, EmployeeDto.class);
    }

    public CompletableFuture<Void> deleteEmployee(Long id) {
        return apiClient.delete("/api/employees/" + id);
    }

    public CompletableFuture<List<ServiceDto>> findServices() {
        return apiClient.get("/api/services", new TypeReference<>() {
        });
    }

    public CompletableFuture<ServiceDto> createService(ServiceRequestDto request) {
        return apiClient.post("/api/services", request, ServiceDto.class);
    }

    public CompletableFuture<ServiceDto> updateService(Long id, ServiceRequestDto request) {
        return apiClient.put("/api/services/" + id, request, ServiceDto.class);
    }

    public CompletableFuture<Void> deleteService(Long id) {
        return apiClient.delete("/api/services/" + id);
    }

    public CompletableFuture<List<AppointmentDto>> findAppointments() {
        return apiClient.get("/api/appointments", new TypeReference<>() {
        });
    }

    public CompletableFuture<AppointmentDto> createAppointment(AppointmentRequestDto request) {
        return apiClient.post("/api/appointments", request, AppointmentDto.class);
    }

    public CompletableFuture<AppointmentDto> updateAppointment(Long id, AppointmentRequestDto request) {
        return apiClient.put("/api/appointments/" + id, request, AppointmentDto.class);
    }

    public CompletableFuture<AppointmentDto> updateAppointmentStatus(Long id, AppointmentStatusRequestDto request) {
        return apiClient.patch("/api/appointments/" + id + "/status", request, AppointmentDto.class);
    }

    public CompletableFuture<Void> deleteAppointment(Long id) {
        return apiClient.delete("/api/appointments/" + id);
    }

    public CompletableFuture<List<InvoiceDto>> findInvoices() {
        return apiClient.get("/api/invoices", new TypeReference<>() {
        });
    }

    public CompletableFuture<InvoiceDto> createInvoice(InvoiceRequestDto request) {
        return apiClient.post("/api/invoices", request, InvoiceDto.class);
    }

    public CompletableFuture<InvoiceDto> payInvoice(Long id, InvoicePaymentRequestDto request) {
        return apiClient.patch("/api/invoices/" + id + "/pay", request, InvoiceDto.class);
    }

    public CompletableFuture<InvoiceDto> updateInvoiceStatus(Long id, InvoiceStatusRequestDto request) {
        return apiClient.patch("/api/invoices/" + id + "/status", request, InvoiceDto.class);
    }

    public CompletableFuture<Void> deleteInvoice(Long id) {
        return apiClient.delete("/api/invoices/" + id);
    }

    public CompletableFuture<List<EmployeeScheduleDto>> findEmployeeSchedules() {
        return apiClient.get("/api/employee-schedules", new TypeReference<>() {
        });
    }

    public CompletableFuture<EmployeeScheduleDto> createEmployeeSchedule(EmployeeScheduleRequestDto request) {
        return apiClient.post("/api/employee-schedules", request, EmployeeScheduleDto.class);
    }

    public CompletableFuture<EmployeeScheduleDto> updateEmployeeSchedule(Long id, EmployeeScheduleRequestDto request) {
        return apiClient.put("/api/employee-schedules/" + id, request, EmployeeScheduleDto.class);
    }

    public CompletableFuture<Void> deleteEmployeeSchedule(Long id) {
        return apiClient.delete("/api/employee-schedules/" + id);
    }

    public CompletableFuture<List<SmsReminderDto>> findSmsReminders() {
        return apiClient.get("/api/sms-reminders", new TypeReference<>() {
        });
    }

    public CompletableFuture<SmsReminderDto> createSmsReminder(SmsReminderRequestDto request) {
        return apiClient.post("/api/sms-reminders", request, SmsReminderDto.class);
    }

    public CompletableFuture<SmsReminderDto> updateSmsReminderStatus(Long id, SmsReminderStatusRequestDto request) {
        return apiClient.patch("/api/sms-reminders/" + id + "/status", request, SmsReminderDto.class);
    }

    public CompletableFuture<Void> deleteSmsReminder(Long id) {
        return apiClient.delete("/api/sms-reminders/" + id);
    }
}
