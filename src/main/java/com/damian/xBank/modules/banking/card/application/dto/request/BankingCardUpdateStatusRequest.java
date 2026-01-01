package com.damian.xBank.modules.banking.card.application.dto.request;

import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import jakarta.validation.constraints.NotNull;

public record BankingCardUpdateStatusRequest(

        @NotNull(
                message = "status must not be null"
        )
        BankingCardStatus status
) {
}
