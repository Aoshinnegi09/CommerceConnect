package com.commerceconnect.dto;

public record InventoryResponse(
        Long id,
        Long productId,
        int quantity,
        int reorderLevel,
        String warehouseLocation,
        Long version
) {}
