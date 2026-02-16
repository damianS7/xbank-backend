package com.damian.xBank.modules.payment.network.transfer.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record IncomingTransferAuthorizationRequest(

    @NotBlank
    String toIban,

    @Positive
    BigDecimal amount,

    @NotBlank
    String currency

) {
}
