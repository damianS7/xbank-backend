package com.damian.xBank.modules.user.customer.domain.exception;

public class CustomerUpdateAuthorizationException extends CustomerException {
    public CustomerUpdateAuthorizationException(String message, Long customerId) {
        super(message, customerId);
    }
}
