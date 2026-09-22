package com.commerceconnect.repository;

import com.commerceconnect.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    long countByCustomerId(Long customerId);
    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    Optional<Order> findByIdempotencyKeyAndCustomerId(String idempotencyKey, Long customerId);
}
