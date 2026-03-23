package com.damian.xBank.modules.payment.intent.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentIntentRequest(
    @NotBlank
    String orderId,

    @NotBlank
    String description,

    @Positive
    BigDecimal amount,
    
    String currency
) {
}
