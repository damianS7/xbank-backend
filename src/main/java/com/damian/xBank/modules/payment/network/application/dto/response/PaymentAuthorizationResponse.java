package com.damian.xBank.modules.payment.network.application.dto.response;

import com.damian.xBank.modules.payment.network.domain.PaymentAuthorizationStatus;

public record PaymentAuthorizationResponse(
        PaymentAuthorizationStatus status,
        String authorizationId,
        String declineReason
) {
}