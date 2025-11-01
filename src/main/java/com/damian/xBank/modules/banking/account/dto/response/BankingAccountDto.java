package com.damian.xBank.modules.banking.account.dto.response;

import com.damian.xBank.modules.banking.account.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.transactions.dto.response.BankingTransactionDto;

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
