package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountNotFoundException extends BankingAccountException {
    public BankingAccountNotFoundException(Long bankingAccountId) {
        this(Exceptions.BANKING.ACCOUNT.NOT_FOUND, bankingAccountId);
    }

    public BankingAccountNotFoundException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }

    public BankingAccountNotFoundException(String message, String bankingAccountNumber) {
        super(message, bankingAccountNumber);
    }
}
