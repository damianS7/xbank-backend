package com.damian.xBank.modules.banking.account.application.usecase.create;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;

public record CreateBankingAccountCommand(
    BankingAccountType type,
    BankingAccountCurrency currency
) {
}
