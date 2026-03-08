package com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request;

import jakarta.validation.constraints.NotNull;

public record ConfirmOutgoingTransferRequest(

    @NotNull(message = "Password must not be null")
    String password
) {
}
