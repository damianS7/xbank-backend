package com.damian.xBank.modules.banking.card.application.cqrs.command;

public record CaptureCardPaymentCommand(
    Long authorizationId
) {
}
