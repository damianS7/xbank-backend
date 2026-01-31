package com.damian.xBank.modules.payment.intent.application.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentIntentRequest(
        @Positive(message = "")
        BigDecimal amount,
        String currency
) {
}
