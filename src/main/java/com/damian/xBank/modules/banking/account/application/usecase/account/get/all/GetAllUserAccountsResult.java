package com.damian.xBank.modules.banking.account.application.usecase.account.get.all;

import com.damian.xBank.modules.banking.account.application.dto.BankingAccountResult;

import java.util.Set;

public record GetAllUserAccountsResult(
    Set<BankingAccountResult> accounts
) {
}
