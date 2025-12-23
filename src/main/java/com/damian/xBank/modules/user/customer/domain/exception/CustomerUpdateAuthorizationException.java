package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class CustomerUpdateAuthorizationException extends CustomerException {
    public CustomerUpdateAuthorizationException(Long customerId) {
        super(ErrorCodes.CUSTOMER_UPDATE_FAILED, customerId);
    }
}
