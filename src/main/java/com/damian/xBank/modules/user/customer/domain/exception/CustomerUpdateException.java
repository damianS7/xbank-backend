package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class CustomerUpdateException extends CustomerException {

    public CustomerUpdateException(Long customerId, Object[] args) {
        super(Exceptions.CUSTOMER_UPDATE_FAILED, customerId, args);
    }
}
