package com.damian.xBank.modules.notification.domain.event;

import com.damian.xBank.modules.notification.domain.enums.NotificationType;

import java.util.Map;

public record NotificationEvent(
        Long recipientId,
        NotificationType type,
        Map<String, Object> metadata,
        String createdAt
) {
}