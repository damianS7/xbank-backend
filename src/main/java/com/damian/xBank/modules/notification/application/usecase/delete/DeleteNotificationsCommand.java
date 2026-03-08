package com.damian.xBank.modules.notification.application.usecase.delete;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteNotificationsCommand(
    @NotEmpty
    List<Long> notificationIds
) {
}
