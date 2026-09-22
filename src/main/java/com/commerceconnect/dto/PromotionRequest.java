package com.commerceconnect.dto;

import java.math.BigDecimal;

public record PromotionRequest(
        String name,
        String description,
        String discountType,
        BigDecimal discountValue,
        String validFrom,
        String validTo,
        boolean active
) {}
