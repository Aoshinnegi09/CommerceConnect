package com.commerceconnect.dto;

public record CampaignRequest(
        String name,
        String description,
        String campaignType,
        String status,
        String startDate,
        String endDate
) {}
