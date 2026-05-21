package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.RoleRequest;
import com.group8.spabooking.dto.response.RoleResponse;
import com.group8.spabooking.entity.Role;
import com.group8.spabooking.exception.BadRequestException;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::from)
                .toList();
    }

    public RoleResponse findById(Long id) {
        return RoleResponse.from(getRole(id));
    }

    @Transactional
    public RoleResponse create(RoleRequest request) {
        String roleName = normalizeRoleName(request.getName());
        if (roleRepository.existsByName(roleName)) {
            throw new BadRequestException("Role đã tồn tại");
        }

        Role role = Role.builder()
                .name(roleName)
                .description(request.getDescription())
                .build();
        return RoleResponse.from(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = getRole(id);
        String roleName = normalizeRoleName(request.getName());

        roleRepository.findByName(roleName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BadRequestException("Role đã tồn tại");
                });

        role.setName(roleName);
        role.setDescription(request.getDescription());
        return RoleResponse.from(role);
    }

    @Transactional
    public void delete(Long id) {
        Role role = getRole(id);
        roleRepository.delete(role);
    }

    public Role getRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role"));
    }

    private String normalizeRoleName(String name) {
        return name.trim().toUpperCase();
    }
}
