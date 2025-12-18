package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class CustomerUpdateException extends CustomerException {

    public CustomerUpdateException(Long customerId, Object[] args) {
        super(ErrorCodes.CUSTOMER_UPDATE_FAILED, customerId, args);
    }
}
