package com.damian.xBank.modules.notification.domain.model;

import java.util.Map;

public record NotificationEvent(
        Long toUserId,
        NotificationType type,
        Map<String, Object> metadata,
        String createdAt
) {
}