package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountNotFoundException extends BankingAccountException {
    public BankingAccountNotFoundException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_NOT_FOUND, bankingAccountId);
    }

    public BankingAccountNotFoundException(String bankingAccountNumber) {
        super(ErrorCodes.BANKING_ACCOUNT_NOT_FOUND, bankingAccountNumber);
    }
}
