package com.damian.xBank.modules.notification.application.usecase.delete;

import java.util.List;

public record DeleteNotificationsCommand(
    List<Long> notificationIds
) {
}
