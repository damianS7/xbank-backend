package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountTransferCurrencyMismatchException extends BankingAccountTransferException {

    public BankingAccountTransferCurrencyMismatchException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_TRANSFER_DIFFERENT_CURRENCY, bankingAccountId);
    }
}
