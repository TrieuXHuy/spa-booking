package com.group8.spabooking.controller;

import com.group8.spabooking.dto.request.BusinessAppointmentRequest;
import com.group8.spabooking.dto.request.BusinessAppointmentStatusRequest;
import com.group8.spabooking.dto.request.BusinessAssignEmployeeRequest;
import com.group8.spabooking.dto.request.BusinessInvoicePaymentRequest;
import com.group8.spabooking.dto.request.BusinessInvoiceRequest;
import com.group8.spabooking.dto.request.BusinessPaymentStatusRequest;
import com.group8.spabooking.dto.request.BusinessSmsReminderRequest;
import com.group8.spabooking.dto.request.LoginRequest;
import com.group8.spabooking.dto.response.AppointmentResponse;
import com.group8.spabooking.dto.response.InvoiceResponse;
import com.group8.spabooking.dto.response.LoginResponse;
import com.group8.spabooking.dto.response.SmsReminderResponse;
import com.group8.spabooking.service.BusinessFlowService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessFlowController {

    private final BusinessFlowService businessFlowService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return businessFlowService.login(request);
    }

    @PostMapping("/appointments/check-employee-availability")
    public Map<String, Object> checkEmployeeAvailability(@Valid @RequestBody BusinessAppointmentRequest request) {
        return businessFlowService.checkEmployeeAvailability(request);
    }

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse createAppointment(@Valid @RequestBody BusinessAppointmentRequest request) {
        return businessFlowService.createAppointment(request);
    }

    @PatchMapping("/appointments/{appointmentId}/assign-employee")
    public AppointmentResponse assignEmployee(
            @PathVariable Long appointmentId,
            @Valid @RequestBody BusinessAssignEmployeeRequest request) {
        return businessFlowService.assignEmployee(
                appointmentId,
                request.getCurrentUserId(),
                request.getEmployeeId());
    }

    @PatchMapping("/appointments/{appointmentId}/status")
    public AppointmentResponse updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @Valid @RequestBody BusinessAppointmentStatusRequest request) {
        return businessFlowService.updateAppointmentStatus(
                appointmentId,
                request.getCurrentUserId(),
                request.getStatus());
    }

    @PostMapping("/invoices")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse createInvoice(@Valid @RequestBody BusinessInvoiceRequest request) {
        return businessFlowService.createInvoice(request);
    }

    @PatchMapping("/invoices/{invoiceId}/pay")
    public InvoiceResponse payInvoice(
            @PathVariable Long invoiceId,
            @Valid @RequestBody BusinessInvoicePaymentRequest request) {
        return businessFlowService.payInvoice(invoiceId, request);
    }

    @PatchMapping("/invoices/{invoiceId}/payment-status")
    public InvoiceResponse updatePaymentStatus(
            @PathVariable Long invoiceId,
            @Valid @RequestBody BusinessPaymentStatusRequest request) {
        return businessFlowService.updatePaymentStatus(invoiceId, request);
    }

    @PostMapping("/sms-reminders")
    @ResponseStatus(HttpStatus.CREATED)
    public SmsReminderResponse generateSmsReminder(@Valid @RequestBody BusinessSmsReminderRequest request) {
        return businessFlowService.generateSmsReminder(request);
    }
}
