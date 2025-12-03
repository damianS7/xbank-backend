package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountInsufficientFundsException extends BankingAccountException {

    public BankingAccountInsufficientFundsException(Long bankingAccountId) {
        this(Exceptions.BANKING.ACCOUNT.INSUFFICIENT_FUNDS, bankingAccountId);
    }

    public BankingAccountInsufficientFundsException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }
}
