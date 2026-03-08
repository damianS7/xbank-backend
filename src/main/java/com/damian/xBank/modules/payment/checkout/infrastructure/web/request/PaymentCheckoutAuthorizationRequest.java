package com.damian.xBank.modules.payment.checkout.infrastructure.web.request;

import java.math.BigDecimal;

public record PaymentCheckoutAuthorizationRequest(
    Long paymentId,
    String cardNumber,
    String cvv,
    String pin,
    BigDecimal amount,
    String returnUrl
) {
}
