package com.commerceconnect.controller;

import com.commerceconnect.dto.CustomerRequest;
import com.commerceconnect.entity.Customer;
import com.commerceconnect.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Customer> createCustomer(@RequestParam Long userId,
                                                  @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createForUser(userId, request));
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }
}
