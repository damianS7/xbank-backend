package com.damian.xBank.modules.banking.account.domain.exception;

public class BankingAccountTransferException extends BankingAccountException {
    public BankingAccountTransferException(String message, Object resourceId) {
        super(message, resourceId);
    }

}
