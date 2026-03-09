package com.damian.xBank.modules.banking.account.application.usecase.request;

import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestCardCommand(
    @Positive
    Long bankingAccountId,
    @NotNull(
        message = "Card type must not be null"
    ) BankingCardType type
) {
}
