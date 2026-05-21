package com.group8.spabooking.controller;

import com.group8.spabooking.dto.request.SmsReminderFailRequest;
import com.group8.spabooking.dto.request.SmsReminderRequest;
import com.group8.spabooking.dto.request.SmsReminderStatusRequest;
import com.group8.spabooking.dto.response.SmsReminderResponse;
import com.group8.spabooking.service.SmsReminderService;
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
@RequestMapping("/api/sms-reminders")
@RequiredArgsConstructor
public class SmsReminderController {

    private final SmsReminderService smsReminderService;

    @GetMapping
    public List<SmsReminderResponse> findAll(
            @RequestParam(required = false) Long appointmentId,
            @RequestParam(required = false) String status) {
        return smsReminderService.findAll(appointmentId, status);
    }

    @GetMapping("/{id}")
    public SmsReminderResponse findById(@PathVariable Long id) {
        return smsReminderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SmsReminderResponse create(@Valid @RequestBody SmsReminderRequest request) {
        return smsReminderService.create(request);
    }

    @PatchMapping("/{id}/sent")
    public SmsReminderResponse markSent(@PathVariable Long id) {
        return smsReminderService.markSent(id);
    }

    @PatchMapping("/{id}/failed")
    public SmsReminderResponse markFailed(
            @PathVariable Long id,
            @Valid @RequestBody SmsReminderFailRequest request) {
        return smsReminderService.markFailed(id, request);
    }

    @PatchMapping("/{id}/status")
    public SmsReminderResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody SmsReminderStatusRequest request) {
        return smsReminderService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        smsReminderService.delete(id);
    }
}
