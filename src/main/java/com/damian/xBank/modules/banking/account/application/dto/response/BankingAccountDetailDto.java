package com.damian.xBank.modules.banking.account.application.dto.response;

import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.transaction.dto.response.BankingTransactionDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record BankingAccountDetailDto(
        Long id,
        String alias,
        String accountNumber,
        BigDecimal balance,
        BankingAccountType accountType,
        BankingAccountCurrency accountCurrency,
        BankingAccountStatus accountStatus,
        Page<BankingTransactionDto> accountTransactions,
        Set<BankingCardDto> accountCards,
        Instant createdAt,
        Instant updatedAt
) {
}
