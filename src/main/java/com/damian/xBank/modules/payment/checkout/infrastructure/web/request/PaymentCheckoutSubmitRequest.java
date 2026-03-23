package com.damian.xBank.modules.payment.checkout.infrastructure.web.request;

public record PaymentCheckoutSubmitRequest(
    Long paymentId,
    String cardHolder,
    String cardNumber,
    String cardCvv,
    String cardPin,
    int expiryMonth,
    int expiryYear
) {
}
