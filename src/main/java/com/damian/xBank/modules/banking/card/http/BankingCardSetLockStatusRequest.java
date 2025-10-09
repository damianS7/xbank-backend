package com.damian.xBank.modules.banking.card.http;

import com.damian.xBank.modules.banking.card.BankingCardLockStatus;
import jakarta.validation.constraints.NotNull;

public record BankingCardSetLockStatusRequest(

        @NotNull(
                message = "Lock status must not be null"
        )
        BankingCardLockStatus lockStatus,
        @NotNull(
                message = "Password must not be null"
        )
        String password
) {
}
