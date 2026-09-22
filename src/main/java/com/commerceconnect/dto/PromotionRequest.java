package com.commerceconnect.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PromotionRequest(
        @NotBlank String name,
        String description,
        @NotBlank String discountType,
        @NotNull @DecimalMin(value = "0.01") BigDecimal discountValue,
        @DecimalMin(value = "0.01") BigDecimal maxDiscountAmount,
        String validFrom,
        String validTo,
        boolean active
) {}
