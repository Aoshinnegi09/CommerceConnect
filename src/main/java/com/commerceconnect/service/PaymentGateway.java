package com.commerceconnect.service;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentGatewayResult charge(BigDecimal amount, String currency, String idempotencyKey);
}
