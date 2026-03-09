package com.damian.xBank.modules.banking.card.application.usecase.capture;

public record CaptureCardPaymentCommand(
    Long authorizationId
) {
}
