package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountDepositException extends BankingAccountException {
    public BankingAccountDepositException(Long bankingAccountId) {
        super(Exceptions.BANKING_ACCOUNT_FAILED_DEPOSIT, bankingAccountId);
    }
}
