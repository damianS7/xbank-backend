package com.damian.xBank.modules.payment.intent.application.cqrs.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentIntentCommand(
    @Positive(message = "")
    BigDecimal amount,

    @NotBlank
    String currency
) {
}
