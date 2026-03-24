package com.damian.xBank.modules.banking.account.application.usecase.set.alias;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;

import java.time.Instant;

public record SetAccountAliasResult(
    Long accountId,
    String alias,
    Instant updatedAt
) {
    public static SetAccountAliasResult from(BankingAccount bankingAccount) {
        return new SetAccountAliasResult(
            bankingAccount.getId(),
            bankingAccount.getAlias(),
            bankingAccount.getUpdatedAt()
        );
    }
}
