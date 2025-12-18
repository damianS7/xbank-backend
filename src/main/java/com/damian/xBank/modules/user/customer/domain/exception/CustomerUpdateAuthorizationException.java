package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class CustomerUpdateAuthorizationException extends CustomerException {
    public CustomerUpdateAuthorizationException(Long customerId) {
        super(Exceptions.CUSTOMER_UPDATE_FAILED, customerId);
    }
}
