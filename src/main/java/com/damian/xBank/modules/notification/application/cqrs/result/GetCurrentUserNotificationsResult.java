package com.damian.xBank.modules.notification.application.cqrs.result;

import org.springframework.data.domain.Page;

public record GetCurrentUserNotificationsResult(
    Page<NotificationResult> pagedResult
) {
}