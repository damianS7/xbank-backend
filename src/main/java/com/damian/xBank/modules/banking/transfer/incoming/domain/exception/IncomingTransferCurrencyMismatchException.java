package com.damian.xBank.modules.banking.transfer.incoming.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class IncomingTransferCurrencyMismatchException extends IncomingTransferException {

    public IncomingTransferCurrencyMismatchException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_TRANSFER_DIFFERENT_CURRENCY, bankingAccountId);
    }
}
