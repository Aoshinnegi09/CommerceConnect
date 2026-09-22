package com.commerceconnect.controller;

import com.commerceconnect.dto.PromotionRequest;
import com.commerceconnect.dto.PromotionResponse;
import com.commerceconnect.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping("/promotions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionRequest request, Authentication authentication) {
        return ResponseEntity.ok(promotionService.createPromotion(request, authentication.getName()));
    }

    @GetMapping("/promotions/{id}")
    public ResponseEntity<PromotionResponse> getPromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @GetMapping("/promotions")
    public ResponseEntity<Page<PromotionResponse>> getPromotions(Pageable pageable) {
        return ResponseEntity.ok(promotionService.getPromotions(pageable));
    }

    @PutMapping("/promotions/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<PromotionResponse> updatePromotion(@PathVariable Long id,
                                                             @Valid @RequestBody PromotionRequest request,
                                                             Authentication authentication) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, request, authentication.getName()));
    }

    @DeleteMapping("/promotions/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING_MANAGER')")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id, Authentication authentication) {
        promotionService.deletePromotion(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
