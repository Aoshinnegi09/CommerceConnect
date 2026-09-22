package com.commerceconnect.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String sku,
        BigDecimal price,
        boolean active,
        CategoryResponse category
) {}
