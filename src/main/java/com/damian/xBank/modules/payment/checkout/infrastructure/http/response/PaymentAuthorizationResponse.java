package com.damian.xBank.modules.payment.checkout.infrastructure.http.response;

import com.damian.xBank.modules.banking.card.application.usecase.authorize.AuthorizeCardPaymentResult;
import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;

public record PaymentAuthorizationResponse(
    PaymentAuthorizationStatus status,
    String authorizationId,
    String declineReason
) {
    public static PaymentAuthorizationResponse from(AuthorizeCardPaymentResult result) {
        return new PaymentAuthorizationResponse(
            result.status(),
            result.authorizationId(),
            result.declineReason()
        );
    }
}