package com.damian.xBank.modules.banking.card.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AuthorizeCardPaymentRequest(
        @NotBlank
        String merchant,

        @NotBlank
        String cardHolder,

        @NotBlank
        String cardNumber,

        @Positive
        Integer expiryMonth,

        @Positive
        Integer expiryYear,

        @NotBlank
        String cvv,

        @Positive
        BigDecimal amount
) {
}
