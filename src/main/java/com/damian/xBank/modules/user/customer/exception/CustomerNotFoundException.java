package com.damian.xBank.modules.user.customer.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class CustomerNotFoundException extends CustomerException {
    public CustomerNotFoundException(Long customerId) {
        super(Exceptions.CUSTOMER.NOT_FOUND, customerId);
    }

    public CustomerNotFoundException(String message, Long customerId) {
        super(message, customerId);
    }
}
