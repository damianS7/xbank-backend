package com.damian.xBank.modules.notification.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class NotificationException extends ApplicationException {

    public NotificationException(String errorCode, Object resourceId) {
        super(errorCode, resourceId, new Object[]{resourceId});
    }
}
