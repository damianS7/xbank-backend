package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountDepositException extends BankingAccountException {
    public BankingAccountDepositException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }

    public BankingAccountDepositException(Long bankingAccountId) {
        this(Exceptions.BANKING.TRANSACTION.FAILED_DEPOSIT, bankingAccountId);
    }
}
