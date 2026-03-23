package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOutgoingTransferRequest(

    @Positive
    Long fromAccountId,

    @NotNull(message = "Banking account number must not be null")
    String toAccountNumber,

    @NotNull(message = "Description must not be null")
    String description,

    @Positive
    BigDecimal amount
) {
}
