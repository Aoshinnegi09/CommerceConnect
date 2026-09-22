package com.commerceconnect.controller;

import com.commerceconnect.dto.OrderRequest;
import com.commerceconnect.dto.OrderResponse;
import com.commerceconnect.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request,
                                                    @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                                    Authentication authentication) {
        return ResponseEntity.ok(orderService.placeOrder(request, authentication.getName(), idempotencyKey));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderById(id, authentication.getName()));
    }

    @GetMapping("/customers/{customerId}/orders")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getCustomerOrders(@PathVariable Long customerId,
                                                                 Pageable pageable,
                                                                 Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrdersForCustomer(customerId, pageable, authentication.getName()));
    }

    @PatchMapping("/orders/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(orderService.cancelOrder(id, authentication.getName()));
    }
}
