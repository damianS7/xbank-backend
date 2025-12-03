package com.damian.xBank.modules.banking.account.application.dto.response;

import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingAccountSummaryDto(
        Long id,
        String alias,
        String accountNumber,
        BigDecimal balance,
        BankingAccountType accountType,
        BankingAccountCurrency accountCurrency,
        BankingAccountStatus accountStatus,
        Long totalCards,
        Instant createdAt,
        Instant updatedAt
) {
}
