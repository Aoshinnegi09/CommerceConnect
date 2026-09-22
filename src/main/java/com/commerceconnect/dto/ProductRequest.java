package com.commerceconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String sku,
        @Positive BigDecimal price,
        Long categoryId,
        boolean active
) {}
