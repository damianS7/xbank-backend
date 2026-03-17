package com.damian.xBank.modules.banking.account.application.usecase.set.alias;

public record SetAccountAliasCommand(
    Long accountId,
    String alias
) {
}
