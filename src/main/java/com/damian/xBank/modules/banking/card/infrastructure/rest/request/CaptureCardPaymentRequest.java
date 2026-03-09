package com.damian.xBank.modules.banking.card.infrastructure.rest.request;

import jakarta.validation.constraints.Positive;

public record CaptureCardPaymentRequest(
    @Positive
    Long authorizationId
) {
}
