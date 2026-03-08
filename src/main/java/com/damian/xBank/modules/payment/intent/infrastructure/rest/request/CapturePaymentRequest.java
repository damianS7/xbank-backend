package com.damian.xBank.modules.payment.intent.infrastructure.rest.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CapturePaymentRequest(
    Long paymentId,
    String merchantName,
    @Positive(message = "")
    BigDecimal amount,
    // returnUrl / callback
    String returnUrl
) {
}
