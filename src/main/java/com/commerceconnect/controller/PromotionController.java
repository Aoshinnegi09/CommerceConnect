package com.commerceconnect.controller;

import com.commerceconnect.dto.PromotionRequest;
import com.commerceconnect.entity.Promotion;
import com.commerceconnect.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Promotion> createPromotion(@Valid @RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.createPromotion(request));
    }

    @GetMapping("/promotions/{id}")
    public ResponseEntity<Promotion> getPromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }
}
