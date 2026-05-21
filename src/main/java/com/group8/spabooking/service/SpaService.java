package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.ServiceRequest;
import com.group8.spabooking.dto.response.ServiceResponse;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.ServiceRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class SpaService {

    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public List<ServiceResponse> findAll() {
        return serviceRepository.findAll().stream()
                .map(ServiceResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceResponse findById(Long id) {
        return ServiceResponse.from(getService(id));
    }

    @Transactional
    public ServiceResponse create(ServiceRequest request) {
        LocalDateTime now = LocalDateTime.now();
        com.group8.spabooking.entity.Service service = com.group8.spabooking.entity.Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationMinutes(request.getDurationMinutes())
                .active(activeOrDefault(request.getActive()))
                .createdAt(now)
                .updatedAt(now)
                .build();

        return ServiceResponse.from(serviceRepository.save(service));
    }

    @Transactional
    public ServiceResponse update(Long id, ServiceRequest request) {
        com.group8.spabooking.entity.Service service = getService(id);
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setActive(activeOrDefault(request.getActive()));
        service.setUpdatedAt(LocalDateTime.now());

        return ServiceResponse.from(service);
    }

    @Transactional
    public void delete(Long id) {
        com.group8.spabooking.entity.Service service = getService(id);
        serviceRepository.delete(service);
    }

    public com.group8.spabooking.entity.Service getService(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ"));
    }

    private Boolean activeOrDefault(Boolean active) {
        return active == null || active;
    }
}
