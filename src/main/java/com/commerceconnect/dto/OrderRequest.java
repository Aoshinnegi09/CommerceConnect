package com.commerceconnect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequest(
        @NotNull Long customerId,
        @NotEmpty List<@Valid OrderItemRequest> items,
        Long promotionId
) {}
