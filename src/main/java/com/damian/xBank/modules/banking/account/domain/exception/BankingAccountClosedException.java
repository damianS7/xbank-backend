package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class BankingAccountClosedException extends BankingAccountException {

    public BankingAccountClosedException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_CLOSED, bankingAccountId);
    }

}
