package com.commerceconnect.controller;

import com.commerceconnect.dto.DashboardSummary;
import com.commerceconnect.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<DashboardSummary> getDashboard() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
