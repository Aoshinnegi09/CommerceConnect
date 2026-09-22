package com.commerceconnect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(
        @NotNull Long productId,
        @Min(0) int quantity,
        @Min(0) int reorderLevel,
        String warehouseLocation
) {}
