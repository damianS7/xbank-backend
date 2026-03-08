package com.damian.xBank.modules.notification.application.usecase.delete;

import jakarta.validation.constraints.Positive;

public record DeleteNotificationCommand(
    @Positive
    Long id
) {
}
