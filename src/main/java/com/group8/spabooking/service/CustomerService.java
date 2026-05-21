package com.group8.spabooking.service;

import com.group8.spabooking.dto.request.CustomerRequest;
import com.group8.spabooking.dto.response.CustomerResponse;
import com.group8.spabooking.entity.Customer;
import com.group8.spabooking.entity.User;
import com.group8.spabooking.exception.ResourceNotFoundException;
import com.group8.spabooking.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getCustomer(id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .user(findUserOrNull(request.getUserId()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .note(request.getNote())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = getCustomer(id);
        customer.setUser(findUserOrNull(request.getUserId()));
        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setGender(request.getGender());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setNote(request.getNote());
        customer.setUpdatedAt(LocalDateTime.now());

        return CustomerResponse.from(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = getCustomer(id);
        customerRepository.delete(customer);
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
    }

    private User findUserOrNull(Long userId) {
        return userId == null ? null : userService.getUser(userId);
    }
}
