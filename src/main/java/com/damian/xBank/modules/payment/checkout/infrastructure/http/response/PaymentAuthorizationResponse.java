package com.damian.xBank.modules.payment.checkout.infrastructure.http.response;

import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;

public record PaymentAuthorizationResponse(
    PaymentAuthorizationStatus status,
    String authorizationId,
    String declineReason
) {
}