package com.damian.xBank.modules.payment.intent.application.dto.response;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentOrderDto(
        Long id,
        PaymentIntentStatus status,
        String merchant,
        BigDecimal amount,
        BankingAccountCurrency currency,
        Instant createdAt,
        Instant updatedAt
) {
}
