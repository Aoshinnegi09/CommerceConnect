package com.commerceconnect.repository;

import com.commerceconnect.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByUserId(Long userId);
}
