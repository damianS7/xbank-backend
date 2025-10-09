package com.damian.xBank.modules.banking.account.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingAccountException extends ApplicationException {
    public BankingAccountException(String message) {
        super(message);
    }
}
