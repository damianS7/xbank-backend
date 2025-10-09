package com.damian.xBank.modules.banking.account;

import com.damian.xBank.modules.banking.card.BankingCardDTO;
import com.damian.xBank.modules.banking.transactions.BankingTransactionDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record BankingAccountDTO(
        Long id,
        String alias,
        String accountNumber,
        BigDecimal balance,
        BankingAccountType accountType,
        BankingAccountCurrency accountCurrency,
        BankingAccountStatus accountStatus,
        Set<BankingTransactionDTO> accountTransactions,
        Set<BankingCardDTO> accountCards,
        Instant createdAt,
        Instant updatedAt
) {
}
