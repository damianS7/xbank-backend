package com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CompleteIncomingTransferRequest(
    @NotBlank
    String authorizationId,

    @NotBlank
    String fromIban,

    @NotBlank
    String toIban,

    @Positive
    BigDecimal amount,

    @NotBlank
    String currency,

    @NotBlank
    String reference
) {
}
