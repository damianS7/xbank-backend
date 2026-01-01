package com.damian.xBank.modules.banking.account.application.dto.request;

import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import jakarta.validation.constraints.NotNull;

public record BankingAccountCardRequest(
        @NotNull(
                message = "Card type must not be null"
        ) BankingCardType type
) {
}
