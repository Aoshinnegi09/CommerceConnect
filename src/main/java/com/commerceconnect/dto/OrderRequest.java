package com.commerceconnect.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest(
        @NotNull Long customerId,
        List<OrderItemRequest> items
) {}
