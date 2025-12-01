package com.damian.xBank.modules.banking.account.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountSuspendedException extends BankingAccountException {

    public BankingAccountSuspendedException(Long bankingAccountId) {
        super(Exceptions.BANKING.ACCOUNT.SUSPENDED, bankingAccountId);
    }
    
}
