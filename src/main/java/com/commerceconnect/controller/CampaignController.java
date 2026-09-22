package com.commerceconnect.controller;

import com.commerceconnect.dto.CampaignRequest;
import com.commerceconnect.entity.Campaign;
import com.commerceconnect.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/campaigns")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<Campaign> createCampaign(@RequestParam Long userId,
                                                  @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok(campaignService.createCampaign(userId, request));
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }
}
