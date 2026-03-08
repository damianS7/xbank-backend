package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransactionNotOwnerException extends BankingTransactionException {

    public BankingTransactionNotOwnerException(Long transactionId, Long customerId) {
        super(ErrorCodes.BANKING_TRANSACTION_NOT_OWNER, transactionId, new Object[]{customerId});
    }
}
