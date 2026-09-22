package com.commerceconnect.dto;

import com.commerceconnect.entity.CampaignStatus;

import java.time.LocalDateTime;

public record CampaignResponse(
        Long id,
        String name,
        String description,
        String campaignType,
        CampaignStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long createdByUserId
) {}
