package com.damian.xBank.modules.notification.infrastructure.rest.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NotificationDeleteRequest(
    @NotEmpty
    List<Long> notificationIds
) {
}
