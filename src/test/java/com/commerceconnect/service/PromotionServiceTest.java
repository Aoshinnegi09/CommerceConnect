package com.commerceconnect.service;

import com.commerceconnect.dto.*;
import com.commerceconnect.entity.*;
import com.commerceconnect.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionService promotionService;

    @Test
    void shouldCalculatePercentageDiscountCorrectly() {
        Promotion promotion = new Promotion();
        promotion.setDiscountType(DiscountType.PERCENTAGE);
        promotion.setDiscountValue(new BigDecimal("10"));
        promotion.setActive(true);
        promotion.setValidFrom(LocalDateTime.now().minusDays(1));
        promotion.setValidTo(LocalDateTime.now().plusDays(1));

        BigDecimal amount = new BigDecimal("1000");
        BigDecimal discount = promotionService.calculateDiscount(amount, promotion);

        assertEquals(new BigDecimal("100.00"), discount);
    }
}
