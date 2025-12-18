package com.damian.xBank.modules.user.customer.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class CustomerImageNotFoundException extends CustomerException {

    public CustomerImageNotFoundException(Long customerId) {
        super(Exceptions.CUSTOMER_IMAGE_NOT_FOUND, customerId);
    }
}
