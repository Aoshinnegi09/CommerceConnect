package com.commerceconnect.service;

import com.commerceconnect.dto.PromotionRequest;
import com.commerceconnect.entity.DiscountType;
import com.commerceconnect.entity.Promotion;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Promotion createPromotion(PromotionRequest request) {
        Promotion promotion = new Promotion();
        promotion.setName(request.name());
        promotion.setDescription(request.description());
        promotion.setDiscountType(DiscountType.valueOf(request.discountType().toUpperCase()));
        promotion.setDiscountValue(request.discountValue());
        promotion.setValidFrom(request.validFrom() == null || request.validFrom().isBlank() ? null : LocalDateTime.parse(request.validFrom()));
        promotion.setValidTo(request.validTo() == null || request.validTo().isBlank() ? null : LocalDateTime.parse(request.validTo()));
        promotion.setActive(request.active());
        return promotionRepository.save(promotion);
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

        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            return amount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        }
        return promotion.getDiscountValue();
    }

    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }
}
