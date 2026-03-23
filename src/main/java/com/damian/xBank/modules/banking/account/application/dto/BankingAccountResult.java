package com.damian.xBank.modules.banking.account.application.dto;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;
import com.damian.xBank.modules.banking.card.infrastructure.mapper.BankingCardDtoMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record BankingAccountResult(
    Long id,
    String alias,
    String accountNumber,
    BigDecimal balance,
    BigDecimal reservedBalance,
    BankingAccountType accountType,
    BankingAccountCurrency accountCurrency,
    BankingAccountStatus accountStatus,
    Set<BankingCardResult> accountCards,
    Instant createdAt,
    Instant updatedAt
) {
    public static BankingAccountResult from(BankingAccount bankingAccount) {
        Set<BankingCardResult> bankingCardsDto = Optional
            .ofNullable(bankingAccount.getBankingCards())
            .orElseGet(Collections::emptySet)
            .stream()
            .map(BankingCardDtoMapper::toBankingCardResult)
            .collect(Collectors.toSet());

        return new BankingAccountResult(
            bankingAccount.getId(),
            bankingAccount.getAlias(),
            bankingAccount.getAccountNumber(),
            bankingAccount.getBalance(),
            bankingAccount.getReservedBalance(),
            bankingAccount.getType(),
            bankingAccount.getCurrency(),
            bankingAccount.getStatus(),
            bankingCardsDto,
            bankingAccount.getCreatedAt(),
            bankingAccount.getUpdatedAt()
        );
    }
}
