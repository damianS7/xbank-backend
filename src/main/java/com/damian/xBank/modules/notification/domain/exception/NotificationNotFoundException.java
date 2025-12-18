package com.damian.xBank.modules.notification.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException(Long notificationId) {
        super(Exceptions.NOTIFICATION_NOT_FOUND, notificationId);
    }
}
