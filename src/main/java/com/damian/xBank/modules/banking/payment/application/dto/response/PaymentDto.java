package com.damian.xBank.modules.banking.payment.application.dto.response;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.payment.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentDto(
        Long id,
        PaymentStatus status,
        String merchant,
        BigDecimal amount,
        BankingAccountCurrency currency,
        Instant createdAt,
        Instant updatedAt
) {
}
