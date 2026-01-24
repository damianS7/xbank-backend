package com.damian.xBank.modules.payment.intent.application.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentCaptureRequest(
        Long paymentId,
        String merchantName,
        @Positive(message = "")
        BigDecimal amount,
        // returnUrl / callback
        String returnUrl
) {
}
