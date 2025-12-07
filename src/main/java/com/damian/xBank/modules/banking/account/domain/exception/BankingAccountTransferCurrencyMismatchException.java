package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountTransferCurrencyMismatchException extends BankingAccountTransferException {
    public BankingAccountTransferCurrencyMismatchException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }

    public BankingAccountTransferCurrencyMismatchException(Long bankingAccountId) {
        this(Exceptions.BANKING.TRANSACTION.DIFFERENT_CURRENCY, bankingAccountId);
    }
}
