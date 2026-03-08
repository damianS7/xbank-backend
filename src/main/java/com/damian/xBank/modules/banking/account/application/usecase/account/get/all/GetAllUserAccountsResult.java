package com.damian.xBank.modules.banking.account.application.usecase.account.get.all;

import com.damian.xBank.modules.banking.account.application.result.BankingAccountResult;

import java.util.Set;

public record GetAllUserAccountsResult(
    Set<BankingAccountResult> accounts
) {
}
