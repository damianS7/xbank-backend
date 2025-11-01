package com.damian.xBank.modules.banking.card.dto.request;

import com.damian.xBank.modules.banking.card.enums.BankingCardType;
import jakarta.validation.constraints.NotNull;

public record BankingCardRequest(
        @NotNull(
                message = "Card type must not be null"
        ) BankingCardType type
) {
}
