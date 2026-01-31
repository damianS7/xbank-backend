package com.damian.xBank.modules.payment.network.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentAuthorizationRequest(

        //        @NotBlank
        //        String paymentId,

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

        @NotBlank
        String pin,

        @Positive
        BigDecimal amount,

        @NotBlank
        String currency,

        @NotBlank
        String description

) {
}
