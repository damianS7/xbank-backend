package com.damian.xBank.modules.banking.card.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CaptureCardPaymentRequest(
        @NotBlank
        Long authorizationId
) {
}
