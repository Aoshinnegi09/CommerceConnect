package com.commerceconnect.dto;

import java.math.BigDecimal;

public record DashboardSummary(
        long customerCount,
        long productCount,
        long orderCount,
        long campaignCount,
        BigDecimal totalRevenue
) {}
