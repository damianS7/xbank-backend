package com.damian.xBank.modules.banking.account.application.dto;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;
import com.damian.xBank.modules.banking.card.infrastructure.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.infrastructure.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record BankingAccountDetailResult(
    Long id,
    String alias,
    String accountNumber,
    BigDecimal balance,
    BankingAccountType accountType,
    BankingAccountCurrency accountCurrency,
    BankingAccountStatus accountStatus,
    PageResult<BankingTransactionResult> accountTransactions,
    Set<BankingCardResult> accountCards,
    Instant createdAt,
    Instant updatedAt
) {
    public static BankingAccountDetailResult from(BankingAccount bankingAccount) {
        // TODO review this
        PageResult<BankingTransactionResult> bankingAccountTransactions = BankingTransactionDtoMapper
            .toBankingTransactionPagedResult(
                bankingAccount.getAccountTransactions()
            );

        Set<BankingCardResult> bankingCardsDto = Optional
            .ofNullable(bankingAccount.getBankingCards())
            .orElseGet(Collections::emptySet)
            .stream()
            .map(BankingCardDtoMapper::toBankingCardResult)
            .collect(Collectors.toSet());

        return new BankingAccountDetailResult(
            bankingAccount.getId(),
            bankingAccount.getAlias(),
            bankingAccount.getAccountNumber(),
            bankingAccount.getBalance(),
            bankingAccount.getType(),
            bankingAccount.getCurrency(),
            bankingAccount.getStatus(),
            bankingAccountTransactions,
            bankingCardsDto,
            bankingAccount.getCreatedAt(),
            bankingAccount.getUpdatedAt()
        );
    }
}
