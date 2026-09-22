package com.commerceconnect.service;

import com.commerceconnect.dto.CustomerRequest;
import com.commerceconnect.dto.CustomerResponse;
import com.commerceconnect.entity.Customer;
import com.commerceconnect.entity.Role;
import com.commerceconnect.entity.User;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.CustomerRepository;
import com.commerceconnect.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public CustomerService(CustomerRepository customerRepository,
                           UserRepository userRepository,
                           AuditService auditService) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public CustomerResponse createForUser(Long requestedUserId, CustomerRequest request, String actorEmail) {
        User actor = userRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        Long targetUserId = requestedUserId == null ? actor.getId() : requestedUserId;
        boolean admin = actor.getRoles().contains(Role.ADMIN);
        if (!admin && !targetUserId.equals(actor.getId())) {
            throw new AccessDeniedException("You cannot create customer profiles for other users.");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        if (customerRepository.findByUserId(targetUserId).isPresent()) {
            throw new IllegalArgumentException("Customer profile already exists for user " + targetUserId);
        }

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        customer.setCity(request.city());
        customer.setState(request.state());
        customer.setCountry(request.country());
        Customer saved = customerRepository.save(customer);
        auditService.log("Customer", saved.getId(), "CREATE", actorEmail, "Created customer profile");
        return DtoMapper.toCustomerResponse(saved);
    }

    public CustomerResponse getByIdAuthorized(Long id, String actorEmail) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        User actor = userRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        boolean admin = actor.getRoles().contains(Role.ADMIN);
        if (!admin && !customer.getUser().getId().equals(actor.getId())) {
            throw new AccessDeniedException("You cannot access other customers' data.");
        }

        return DtoMapper.toCustomerResponse(customer);
    }
}
