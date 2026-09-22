package com.commerceconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CategoryRequest(
        @NotBlank String name,
        String description
) {}
