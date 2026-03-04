package com.damian.xBank.modules.banking.account.application.cqrs.command;

public record ActivateBankingAccountCommand(
    Long accountId
) {
}
