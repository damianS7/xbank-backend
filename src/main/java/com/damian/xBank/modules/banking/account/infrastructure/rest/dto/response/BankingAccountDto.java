package com.damian.xBank.modules.banking.account.infrastructure.rest.dto.response;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.cqrs.result.BankingCardResult;
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
    Set<BankingCardResult> accountCards,
    Instant createdAt,
    Instant updatedAt
) {
}
