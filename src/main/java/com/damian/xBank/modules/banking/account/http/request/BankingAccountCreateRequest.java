package com.damian.xBank.modules.banking.account.http.request;

import com.damian.xBank.modules.banking.account.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.BankingAccountType;
import jakarta.validation.constraints.NotNull;

public record BankingAccountCreateRequest(
        @NotNull(message = "Account type must not be null")
        BankingAccountType accountType,

        @NotNull(message = "Account currency must not be null")
        BankingAccountCurrency accountCurrency
) {
}
