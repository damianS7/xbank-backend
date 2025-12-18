package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransactionOwnershipException extends BankingTransactionException {

    public BankingTransactionOwnershipException(Long transactionId, Long customerId) {
        super(ErrorCodes.BANKING_TRANSACTION_OWNERSHIP, transactionId, new Object[]{customerId});
    }
}
