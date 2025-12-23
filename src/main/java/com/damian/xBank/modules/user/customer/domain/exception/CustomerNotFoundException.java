package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class CustomerNotFoundException extends CustomerException {
    public CustomerNotFoundException(Long customerId) {
        super(ErrorCodes.CUSTOMER_NOT_FOUND, customerId);
    }
}
