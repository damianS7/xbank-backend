package com.damian.xBank.modules.banking.account.application.dto.request;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import jakarta.validation.constraints.NotNull;

public record BankingAccountCreateRequest(
        @NotNull(message = "Account type must not be null")
        BankingAccountType type,

        @NotNull(message = "Account currency must not be null")
        BankingAccountCurrency currency
) {
}
