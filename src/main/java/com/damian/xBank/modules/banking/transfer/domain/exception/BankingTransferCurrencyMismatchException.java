package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransferCurrencyMismatchException extends BankingTransferException {

    public BankingTransferCurrencyMismatchException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_TRANSFER_DIFFERENT_CURRENCY, bankingAccountId);
    }
}
