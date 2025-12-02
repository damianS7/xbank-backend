package com.damian.xBank.modules.notification.event;

import com.damian.xBank.modules.notification.enums.NotificationType;

import java.util.Map;

public record NotificationEvent(
        Long recipientId,
        NotificationType type,
        Map<String, Object> metadata,
        String createdAt
) {
}