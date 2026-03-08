package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransactionNotPendingStatusException extends BankingTransactionException {
    public BankingTransactionNotPendingStatusException(Long transactionId) {
        super(ErrorCodes.BANKING_TRANSACTION_NOT_AUTHORIZED, transactionId);
    }
}
