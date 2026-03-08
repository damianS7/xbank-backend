package com.damian.xBank.modules.banking.account.infrastructure.rest.request;

import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import jakarta.validation.constraints.NotNull;

public record RequestBankingAccountCardRequest(
    @NotNull(
        message = "Card type must not be null"
    ) BankingCardType type
) {
}
