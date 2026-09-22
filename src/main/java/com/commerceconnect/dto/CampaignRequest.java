package com.commerceconnect.dto;

import jakarta.validation.constraints.NotBlank;

public record CampaignRequest(
        @NotBlank String name,
        String description,
        @NotBlank String campaignType,
        @NotBlank String status,
        String startDate,
        String endDate
) {}
