package com.damian.xBank.modules.notification.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class NotificationException extends ApplicationException {
    private final Long notificationId;
    private final Long userId;

    public NotificationException(String message) {
        this(message, null, null);
    }

    public NotificationException(String message, Long notificationId, Long userId) {
        super(message);
        this.userId = userId;
        this.notificationId = notificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getUserId() {
        return userId;
    }
}
