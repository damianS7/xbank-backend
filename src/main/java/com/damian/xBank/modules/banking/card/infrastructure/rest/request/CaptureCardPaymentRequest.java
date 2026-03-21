package com.damian.xBank.modules.banking.card.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;

public record CaptureCardPaymentRequest(
    @NotBlank
    String authorizationId
) {
}
