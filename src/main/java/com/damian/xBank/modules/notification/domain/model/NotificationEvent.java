package com.damian.xBank.modules.notification.domain.model;

import java.time.Instant;
import java.util.Map;

public record NotificationEvent(
        Long toUserId,
        NotificationType type,
        Map<String, Object> payload,
        Instant createdAt
) {
}