package com.damian.xBank.modules.notification.application.cqrs.command;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteNotificationsCommand(
    @NotEmpty
    List<Long> notificationIds
) {
}
