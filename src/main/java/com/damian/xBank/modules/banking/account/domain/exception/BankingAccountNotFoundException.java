package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountNotFoundException extends BankingAccountException {
    public BankingAccountNotFoundException(Long bankingAccountId) {
        super(Exceptions.BANKING_ACCOUNT_NOT_FOUND, bankingAccountId);
    }

    public BankingAccountNotFoundException(String bankingAccountNumber) {
        super(Exceptions.BANKING_ACCOUNT_NOT_FOUND, bankingAccountNumber);
    }
}
