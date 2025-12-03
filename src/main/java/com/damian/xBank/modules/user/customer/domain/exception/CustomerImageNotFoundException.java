package com.damian.xBank.modules.user.customer.domain.exception;

public class CustomerImageNotFoundException extends CustomerException {

    public CustomerImageNotFoundException(String message, Long customerId) {
        super(message, customerId);
    }
}
