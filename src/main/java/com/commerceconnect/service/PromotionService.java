package com.commerceconnect.service;

import com.commerceconnect.dto.PromotionRequest;
import com.commerceconnect.dto.PromotionResponse;
import com.commerceconnect.entity.DiscountType;
import com.commerceconnect.entity.Promotion;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.PromotionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final AuditService auditService;

    public PromotionService(PromotionRepository promotionRepository, AuditService auditService) {
        this.promotionRepository = promotionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request, String actorEmail) {
        Promotion promotion = mapRequest(new Promotion(), request);
        Promotion saved = promotionRepository.save(promotion);
        auditService.log("Promotion", saved.getId(), "CREATE", actorEmail, "Created promotion " + saved.getName());
        return DtoMapper.toPromotionResponse(saved);
    }

    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionRequest request, String actorEmail) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
        Promotion saved = promotionRepository.save(mapRequest(promotion, request));
        auditService.log("Promotion", saved.getId(), "UPDATE", actorEmail, "Updated promotion " + saved.getName());
        return DtoMapper.toPromotionResponse(saved);
    }

    @Transactional
    public void deletePromotion(Long id, String actorEmail) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
        promotion.setActive(false);
        promotionRepository.save(promotion);
        auditService.log("Promotion", id, "DEACTIVATE", actorEmail, "Deactivated promotion " + promotion.getName());
    }

    public PromotionResponse getPromotionById(Long id) {
        return DtoMapper.toPromotionResponse(promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id)));
    }

    public Page<PromotionResponse> getPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable).map(DtoMapper::toPromotionResponse);
    }

    public BigDecimal calculateDiscount(BigDecimal amount, Promotion promotion) {
        if (promotion == null || !promotion.isActive()) {
            return BigDecimal.ZERO;
        }

        if (promotion.getValidFrom() != null && LocalDateTime.now().isBefore(promotion.getValidFrom())) {
            return BigDecimal.ZERO;
        }
        if (promotion.getValidTo() != null && LocalDateTime.now().isAfter(promotion.getValidTo())) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = amount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (promotion.getMaxDiscountAmount() != null && discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
                discount = promotion.getMaxDiscountAmount();
            }
        } else {
            discount = promotion.getDiscountValue();
        }

        return discount.min(amount);
    }

    public Promotion getPromotionEntityById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }

    private Promotion mapRequest(Promotion promotion, PromotionRequest request) {
        DiscountType discountType;
        try {
            discountType = DiscountType.valueOf(request.discountType().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid discount type. Allowed values: PERCENTAGE, FIXED.");
        }

        if (discountType == DiscountType.PERCENTAGE && request.discountValue().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100.");
        }

        LocalDateTime validFrom = request.validFrom() == null || request.validFrom().isBlank() ? null : LocalDateTime.parse(request.validFrom());
        LocalDateTime validTo = request.validTo() == null || request.validTo().isBlank() ? null : LocalDateTime.parse(request.validTo());
        if (validFrom != null && validTo != null && validFrom.isAfter(validTo)) {
            throw new IllegalArgumentException("validFrom must be before validTo.");
        }

        promotion.setName(request.name());
        promotion.setDescription(request.description());
        promotion.setDiscountType(discountType);
        promotion.setDiscountValue(request.discountValue());
        promotion.setMaxDiscountAmount(request.maxDiscountAmount());
        promotion.setValidFrom(validFrom);
        promotion.setValidTo(validTo);
        promotion.setActive(request.active());
        return promotion;
    }
}
