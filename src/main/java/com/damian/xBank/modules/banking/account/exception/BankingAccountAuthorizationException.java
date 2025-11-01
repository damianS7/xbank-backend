package com.damian.xBank.modules.banking.account.exception;

public class BankingAccountAuthorizationException extends BankingAccountException {
    private Long customerId;

    public BankingAccountAuthorizationException(String message, Long bankingAccountId, Long customerId) {
        super(message, bankingAccountId);
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
