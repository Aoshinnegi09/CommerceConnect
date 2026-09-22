package com.commerceconnect.dto;

import com.commerceconnect.entity.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionResponse(
        Long id,
        String name,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        boolean active
) {}
