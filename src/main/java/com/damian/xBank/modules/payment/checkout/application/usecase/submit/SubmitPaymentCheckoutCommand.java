package com.damian.xBank.modules.payment.checkout.application.usecase.submit;

public record SubmitPaymentCheckoutCommand(
    Long paymentId,
    String cardHolder,
    String cardNumber,
    String cardCvv,
    String cardPin,
    int expiryMonth,
    int expiryYear
) {
}
