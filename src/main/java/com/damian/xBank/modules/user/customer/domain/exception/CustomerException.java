package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class CustomerException extends ApplicationException {
    public CustomerException(String errorCode, Object resourceId) {
        this(errorCode, resourceId, new Object[]{resourceId});
    }

    public CustomerException(String errorCode, Object resourceId, Object[] args) {
        super(errorCode, resourceId, args);
    }
}
