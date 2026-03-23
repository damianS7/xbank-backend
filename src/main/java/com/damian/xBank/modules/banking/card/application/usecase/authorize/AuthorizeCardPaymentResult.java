package com.damian.xBank.modules.banking.card.application.usecase.authorize;

import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;

public record AuthorizeCardPaymentResult(
    PaymentAuthorizationStatus status,
    String authorizationId,
    String declineReason
) {
}