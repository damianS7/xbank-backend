package com.damian.xBank.modules.user.customer.exception;

public class CustomerUpdateException extends CustomerException {
    private final String key;
    private final String value;

    public CustomerUpdateException(String message, Long customerId, String key, String value) {
        super(message, customerId);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
