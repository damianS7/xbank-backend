package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransactionStatusTransitionException extends BankingTransactionException {
    public BankingTransactionStatusTransitionException(String fromStatus, String toStatus) {
        this(new Object[]{fromStatus, toStatus});
    }

    public BankingTransactionStatusTransitionException(Object[] args) {
        super(ErrorCodes.BANKING_TRANSACTION_INVALID_TRANSITION_STATUS, args);
    }
}
