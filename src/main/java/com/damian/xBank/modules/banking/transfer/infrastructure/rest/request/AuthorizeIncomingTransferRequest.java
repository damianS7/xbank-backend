package com.damian.xBank.modules.banking.transfer.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AuthorizeIncomingTransferRequest(

    @NotBlank
    String toIban,

    @Positive
    BigDecimal amount,

    @NotBlank
    String currency

) {
}
