package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingAccountException extends ApplicationException {

    public BankingAccountException(String message, Object resourceId) {
        super(message, resourceId, new Object[]{resourceId});
    }

    public BankingAccountException(String message, Object resourceId, Object[] args) {
        super(message, resourceId, args);
    }
}
