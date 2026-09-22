package com.commerceconnect.dto;

import com.commerceconnect.entity.OrderStatus;
import com.commerceconnect.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        Long promotionId,
        OrderStatus status,
        PaymentStatus paymentStatus,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {}
