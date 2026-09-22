package com.commerceconnect.service;

import com.commerceconnect.entity.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public PaymentGatewayResult charge(BigDecimal amount, String currency, String idempotencyKey) {
        if (amount == null || amount.signum() <= 0) {
            return new PaymentGatewayResult(PaymentStatus.FAILED, "TXN-INVALID");
        }
        return new PaymentGatewayResult(
                PaymentStatus.PAID,
                "TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase()
        );
    }
}
