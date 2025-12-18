package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class CustomerNotFoundException extends CustomerException {
    public CustomerNotFoundException(Long customerId) {
        super(Exceptions.CUSTOMER_NOT_FOUND, customerId);
    }
}
