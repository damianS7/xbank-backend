package com.damian.xBank.modules.payment.network.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentAuthorizationRequest(

        //        @NotBlank
        //        String paymentId,

        @NotBlank
        String merchantName,

        @NotBlank
        String cardNumber,

        @NotNull
        Integer expiryMonth,

        @NotNull
        Integer expiryYear,

        @NotBlank
        String cvv,

        @NotBlank
        String pin,

        @Positive
        BigDecimal amount

) {
}
