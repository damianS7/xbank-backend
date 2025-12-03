package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class CustomerException extends ApplicationException {
    private final Long customerId;

    public CustomerException(String message) {
        this(message, null);
    }

    public CustomerException(String message, Long customerId) {
        super(message);
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
