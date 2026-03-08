package com.damian.xBank.modules.notification.application.usecase.get;

import com.damian.xBank.modules.notification.application.usecase.NotificationResult;
import org.springframework.data.domain.Page;

public record GetCurrentUserNotificationsResult(
    Page<NotificationResult> pagedResult
) {
}