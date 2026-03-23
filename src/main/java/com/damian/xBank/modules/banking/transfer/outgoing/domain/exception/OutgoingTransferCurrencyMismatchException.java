package com.damian.xBank.modules.banking.transfer.outgoing.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class OutgoingTransferCurrencyMismatchException extends OutgoingTransferException {

    public OutgoingTransferCurrencyMismatchException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_TRANSFER_DIFFERENT_CURRENCY, bankingAccountId);
    }
}
