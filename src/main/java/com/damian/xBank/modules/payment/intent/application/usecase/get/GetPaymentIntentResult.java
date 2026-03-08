package com.damian.xBank.modules.payment.intent.application.usecase.get;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record GetPaymentIntentResult(
    Long id,
    PaymentIntentStatus status,
    String merchant,
    String merchantCallbackUrl,
    BigDecimal amount,
    BankingAccountCurrency currency,
    Instant createdAt,
    Instant updatedAt
) {
    public static GetPaymentIntentResult from(PaymentIntent paymentIntent) {
        return new GetPaymentIntentResult(
            paymentIntent.getId(),
            paymentIntent.getStatus(),
            paymentIntent.getMerchantName(),
            paymentIntent.getMerchantCallbackUrl(),
            paymentIntent.getAmount(),
            paymentIntent.getCurrency(),
            paymentIntent.getCreatedAt(),
            paymentIntent.getUpdatedAt()
        );
    }
}
