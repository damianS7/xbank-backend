package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountTransferCurrencyMismatchException extends BankingAccountTransferException {

    public BankingAccountTransferCurrencyMismatchException(Long bankingAccountId) {
        super(Exceptions.BANKING_ACCOUNT_DIFFERENT_CURRENCY, bankingAccountId);
    }
}
