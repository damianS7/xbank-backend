package com.damian.xBank.modules.banking.account.dto.response;

import com.damian.xBank.modules.banking.account.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.enums.BankingAccountType;

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
