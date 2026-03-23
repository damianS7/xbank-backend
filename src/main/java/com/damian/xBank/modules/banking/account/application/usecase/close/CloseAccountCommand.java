package com.damian.xBank.modules.banking.account.application.usecase.close;

public record CloseAccountCommand(
    Long accountId,
    String password
) {
}
