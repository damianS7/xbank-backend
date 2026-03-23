package com.damian.xBank.modules.payment.intent.application.usecase.create;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record CreatePaymentIntentResult(
    Long id,
    PaymentIntentStatus status,
    String merchant,
    BigDecimal amount,
    BankingAccountCurrency currency,
    Instant createdAt,
    Instant updatedAt
) {
    public static CreatePaymentIntentResult from(PaymentIntent paymentIntent) {
        return new CreatePaymentIntentResult(
            paymentIntent.getId(),
            paymentIntent.getStatus(),
            paymentIntent.getMerchant().getMerchantName(),
            paymentIntent.getAmount(),
            paymentIntent.getCurrency(),
            paymentIntent.getCreatedAt(),
            paymentIntent.getUpdatedAt()
        );
    }
}
