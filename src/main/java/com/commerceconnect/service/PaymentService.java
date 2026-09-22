package com.commerceconnect.service;

import com.commerceconnect.entity.Order;
import com.commerceconnect.entity.Payment;
import com.commerceconnect.entity.PaymentStatus;
import com.commerceconnect.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public PaymentService(PaymentRepository paymentRepository, PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public Payment process(Order order, BigDecimal amount, String idempotencyKey) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod("CARD");
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        payment.setStatus(PaymentStatus.AUTHORIZED);
        payment = paymentRepository.save(payment);

        PaymentGatewayResult result = paymentGateway.charge(amount, "INR", idempotencyKey);
        payment.setStatus(result.status());
        payment.setTransactionReference(result.transactionReference());
        return paymentRepository.save(payment);
    }
}
