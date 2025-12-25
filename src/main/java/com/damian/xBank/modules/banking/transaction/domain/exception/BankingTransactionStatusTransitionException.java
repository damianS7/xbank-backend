package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransactionStatusTransitionException extends BankingTransactionException {

    public BankingTransactionStatusTransitionException(
            Long transactionId, String fromStatus, String toStatus
    ) {
        this(transactionId, new Object[]{fromStatus, toStatus});
    }

    public BankingTransactionStatusTransitionException(Long transactionId, Object[] args) {
        super(ErrorCodes.BANKING_TRANSACTION_INVALID_TRANSITION_STATUS, transactionId, args);
    }
}
