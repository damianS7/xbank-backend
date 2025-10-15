package com.damian.xBank.modules.user.customer.exception;

public class CustomerNotFoundException extends CustomerException {
    public CustomerNotFoundException(String message, Long customerId) {
        super(message, customerId);
    }
}
