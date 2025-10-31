package com.damian.xBank.modules.banking.account.dto.request;

import com.damian.xBank.modules.banking.account.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.enums.BankingAccountType;
import jakarta.validation.constraints.NotNull;

public record BankingAccountCreateRequest(
        @NotNull(message = "Account type must not be null")
        BankingAccountType type,

        @NotNull(message = "Account currency must not be null")
        BankingAccountCurrency currency
) {
}
