package com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferAuthorizationRequest(

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
