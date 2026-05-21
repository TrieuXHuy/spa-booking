package com.group8.spabooking.controller;

import com.group8.spabooking.dto.request.InvoicePaymentRequest;
import com.group8.spabooking.dto.request.InvoiceRequest;
import com.group8.spabooking.dto.request.InvoiceStatusRequest;
import com.group8.spabooking.dto.response.InvoiceResponse;
import com.group8.spabooking.service.InvoiceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public List<InvoiceResponse> findAll() {
        return invoiceService.findAll();
    }

    @GetMapping("/{id}")
    public InvoiceResponse findById(@PathVariable Long id) {
        return invoiceService.findById(id);
    }

    @GetMapping("/by-appointment")
    public InvoiceResponse findByAppointmentId(@RequestParam Long appointmentId) {
        return invoiceService.findByAppointmentId(appointmentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse create(@Valid @RequestBody InvoiceRequest request) {
        return invoiceService.create(request);
    }

    @PatchMapping("/{id}/pay")
    public InvoiceResponse pay(@PathVariable Long id, @Valid @RequestBody InvoicePaymentRequest request) {
        return invoiceService.pay(id, request);
    }

    @PatchMapping("/{id}/status")
    public InvoiceResponse updateStatus(@PathVariable Long id, @Valid @RequestBody InvoiceStatusRequest request) {
        return invoiceService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        invoiceService.delete(id);
    }
}
