package com.damian.xBank.modules.banking.account.application.dto.response;

import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record BankingAccountDto(
        Long id,
        String alias,
        String accountNumber,
        BigDecimal balance,
        BankingAccountType accountType,
        BankingAccountCurrency accountCurrency,
        BankingAccountStatus accountStatus,
        Set<BankingTransactionDto> accountTransactions,
        Set<BankingCardDto> accountCards,
        Instant createdAt,
        Instant updatedAt
) {
}
