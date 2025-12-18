package com.damian.xBank.modules.notification.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException(Long notificationId) {
        super(ErrorCodes.NOTIFICATION_NOT_FOUND, notificationId);
    }
}
