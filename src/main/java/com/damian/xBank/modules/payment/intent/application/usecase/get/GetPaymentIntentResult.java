package com.damian.xBank.modules.payment.intent.application.usecase.get;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record GetPaymentIntentResult(
    Long id,
    String orderId,
    PaymentIntentStatus status,
    String merchant,
    String merchantCallbackUrl,
    BigDecimal amount,
    BankingAccountCurrency currency,
    String paymentDescription,
    Instant createdAt,
    Instant updatedAt
) {
    public static GetPaymentIntentResult from(PaymentIntent paymentIntent) {
        return new GetPaymentIntentResult(
            paymentIntent.getId(),
            paymentIntent.getOrderId(),
            paymentIntent.getStatus(),
            paymentIntent.getMerchant().getMerchantName(),
            paymentIntent.getMerchant().getCallbackUrl(),
            paymentIntent.getAmount(),
            paymentIntent.getCurrency(),
            paymentIntent.getDescription(),
            paymentIntent.getCreatedAt(),
            paymentIntent.getUpdatedAt()
        );
    }
}
