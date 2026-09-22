package com.commerceconnect.service;

import com.commerceconnect.entity.PaymentStatus;

public record PaymentGatewayResult(PaymentStatus status, String transactionReference) {
}
