package com.damian.xBank.modules.banking.account.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountClosedException extends BankingAccountException {

    public BankingAccountClosedException(Long bankingAccountId) {
        super(Exceptions.BANKING.ACCOUNT.CLOSED, bankingAccountId);
    }

}
