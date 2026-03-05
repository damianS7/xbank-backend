package com.damian.xBank.modules.payment.network.card.infrastructure.web.dto.response;

import com.damian.xBank.modules.payment.network.card.domain.PaymentAuthorizationStatus;

public record PaymentAuthorizationResult(
    PaymentAuthorizationStatus status,
    String authorizationId,
    String declineReason
) {
}