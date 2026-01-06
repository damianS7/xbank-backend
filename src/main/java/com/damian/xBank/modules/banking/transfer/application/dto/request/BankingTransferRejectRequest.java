package com.damian.xBank.modules.banking.transfer.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record BankingTransferRejectRequest(

        @NotNull(message = "Password must not be null")
        String password
) {
}
