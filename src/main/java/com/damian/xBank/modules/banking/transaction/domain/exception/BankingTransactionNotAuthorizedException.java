package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransactionNotAuthorizedException extends BankingTransactionException {
    public BankingTransactionNotAuthorizedException(Long transactionId) {
        super(ErrorCodes.BANKING_TRANSACTION_NOT_AUTHORIZED, transactionId);
    }
}
