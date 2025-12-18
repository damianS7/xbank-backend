package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountInsufficientFundsException extends BankingAccountException {

    public BankingAccountInsufficientFundsException(Long bankingAccountId) {
        super(Exceptions.BANKING_ACCOUNT_INSUFFICIENT_FUNDS, bankingAccountId);
    }
}
