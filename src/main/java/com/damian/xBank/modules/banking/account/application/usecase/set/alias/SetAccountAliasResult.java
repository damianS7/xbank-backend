package com.damian.xBank.modules.banking.account.application.usecase.set.alias;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;
import com.damian.xBank.modules.banking.card.infrastructure.mapper.BankingCardDtoMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record SetAccountAliasResult(
    Long id,
    String alias,
    String accountNumber,
    BigDecimal balance,
    BankingAccountType accountType,
    BankingAccountCurrency accountCurrency,
    BankingAccountStatus accountStatus,
    Set<BankingCardResult> accountCards,
    Instant createdAt,
    Instant updatedAt
) {
    public static SetAccountAliasResult from(BankingAccount bankingAccount) {
        return new SetAccountAliasResult(
            bankingAccount.getId(),
            bankingAccount.getAlias(),
            bankingAccount.getAccountNumber(),
            bankingAccount.getBalance(),
            bankingAccount.getType(),
            bankingAccount.getCurrency(),
            bankingAccount.getStatus(),
            BankingCardDtoMapper.toBankingCardResultSetDTO(bankingAccount.getBankingCards()),
            bankingAccount.getCreatedAt(),
            bankingAccount.getUpdatedAt()
        );
    }
}
