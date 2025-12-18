package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class CustomerImageNotFoundException extends CustomerException {

    public CustomerImageNotFoundException(Long customerId) {
        super(ErrorCodes.CUSTOMER_IMAGE_NOT_FOUND, customerId);
    }
}
