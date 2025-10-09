package com.damian.xBank.modules.customer.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class CustomerException extends ApplicationException {
    public CustomerException(String message) {
        super(message);
    }
}
