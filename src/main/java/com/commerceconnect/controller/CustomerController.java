package com.commerceconnect.controller;

import com.commerceconnect.dto.CustomerRequest;
import com.commerceconnect.dto.CustomerResponse;
import com.commerceconnect.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<CustomerResponse> createCustomer(@RequestParam(required = false) Long userId,
                                                           @Valid @RequestBody CustomerRequest request,
                                                           Authentication authentication) {
        return ResponseEntity.ok(customerService.createForUser(userId, request, authentication.getName()));
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(customerService.getByIdAuthorized(id, authentication.getName()));
    }
}
