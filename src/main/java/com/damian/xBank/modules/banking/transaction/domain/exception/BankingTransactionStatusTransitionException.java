package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingTransactionStatusTransitionException extends BankingTransactionException {
    public BankingTransactionStatusTransitionException(Long transactionId, Object[] args) {
        super(Exceptions.BANKING_TRANSACTION_INVALID_TRANSITION_STATUS, transactionId, args);
    }
    // TODO
    //    public BankingTransactionStatusTransitionException(Object[] args) {
    //        super(Exceptions.BANKING_TRANSACTION_INVALID_TRANSITION_STATUS, args);
    //    }
}
