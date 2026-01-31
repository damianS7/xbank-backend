package com.damian.xBank.modules.payment.checkout.application.dto.request;

public record PaymentCheckoutSubmitRequest(
        Long paymentId,
        String cardHolder,
        String cardNumber,
        String cardCvv,
        String cardPin,
        int expiryMonth,
        int expiryYear,
        String redirectToUrl
) {
}
