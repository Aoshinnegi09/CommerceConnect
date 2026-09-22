package com.commerceconnect.controller;

import com.commerceconnect.dto.CampaignRequest;
import com.commerceconnect.dto.CampaignResponse;
import com.commerceconnect.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<CampaignResponse> createCampaign(@RequestParam Long userId,
                                                           @Valid @RequestBody CampaignRequest request,
                                                           Authentication authentication) {
        return ResponseEntity.ok(campaignService.createCampaign(userId, request, authentication.getName()));
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<CampaignResponse> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @GetMapping("/campaigns")
    public ResponseEntity<Page<CampaignResponse>> getCampaigns(Pageable pageable) {
        return ResponseEntity.ok(campaignService.getCampaigns(pageable));
    }

    @PutMapping("/campaigns/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<CampaignResponse> updateCampaign(@PathVariable Long id,
                                                           @Valid @RequestBody CampaignRequest request,
                                                           Authentication authentication) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, request, authentication.getName()));
    }

    @DeleteMapping("/campaigns/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id, Authentication authentication) {
        campaignService.deleteCampaign(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
