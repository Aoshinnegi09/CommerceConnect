package com.commerceconnect.service;

import com.commerceconnect.dto.CustomerRequest;
import com.commerceconnect.dto.DashboardSummary;
import com.commerceconnect.entity.Customer;
import com.commerceconnect.entity.User;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.CustomerRepository;
import com.commerceconnect.repository.OrderRepository;
import com.commerceconnect.repository.ProductRepository;
import com.commerceconnect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CustomerService(CustomerRepository customerRepository,
                          UserRepository userRepository,
                          OrderRepository orderRepository,
                          ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Customer createForUser(Long userId, CustomerRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (customerRepository.findByUserId(userId) != null) {
            throw new IllegalArgumentException("Customer profile already exists for user " + userId);
        }

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        customer.setCity(request.city());
        customer.setState(request.state());
        customer.setCountry(request.country());
        return customerRepository.save(customer);
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public DashboardSummary getDashboardSummary() {
        long customerCount = customerRepository.count();
        long productCount = productRepository.count();
        long orderCount = orderRepository.count();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        return new DashboardSummary(customerCount, productCount, orderCount, 0L, totalRevenue);
    }
}
