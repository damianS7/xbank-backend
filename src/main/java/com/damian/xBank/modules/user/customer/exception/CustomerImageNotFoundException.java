package com.damian.xBank.modules.user.customer.exception;

public class CustomerImageNotFoundException extends CustomerException {

    public CustomerImageNotFoundException(String message, Long customerId) {
        super(message, customerId);
    }
}
