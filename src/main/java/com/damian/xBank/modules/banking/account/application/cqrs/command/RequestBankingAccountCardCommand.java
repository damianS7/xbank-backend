package com.damian.xBank.modules.banking.account.application.cqrs.command;

import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestBankingAccountCardCommand(
    @Positive
    Long bankingAccountId,
    @NotNull(
        message = "Card type must not be null"
    ) BankingCardType type
) {
}
