package com.damian.xBank.modules.notification.application.dto.request;

import java.util.List;

public record NotificationDeleteRequest(
        List<Long> notificationIds
) {
}
