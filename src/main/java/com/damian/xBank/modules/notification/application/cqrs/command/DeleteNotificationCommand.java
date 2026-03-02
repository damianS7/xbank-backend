package com.damian.xBank.modules.notification.application.cqrs.command;

import jakarta.validation.constraints.Positive;

public record DeleteNotificationCommand(
    @Positive
    Long id
) {
}
