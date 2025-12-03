package com.damian.xBank.modules.banking.card.application.dto.request;

import com.damian.xBank.modules.banking.card.domain.enums.BankingCardType;
import jakarta.validation.constraints.NotNull;

public record BankingCardRequest(
        @NotNull(
                message = "Card type must not be null"
        ) BankingCardType type
) {
}
