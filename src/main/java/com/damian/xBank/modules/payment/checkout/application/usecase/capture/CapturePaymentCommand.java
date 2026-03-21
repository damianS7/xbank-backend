package com.damian.xBank.modules.payment.checkout.application.usecase.capture;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CapturePaymentCommand(
    Long paymentId,
    String merchantName,
    @Positive(message = "")
    BigDecimal amount,
    // returnUrl / callback
    String returnUrl
) {
}
