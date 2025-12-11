package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingTransactionOwnershipException extends BankingTransactionException {
    private Long customerId;

    public BankingTransactionOwnershipException(String message, Long transactionId, Long customerId) {
        super(message, transactionId);
        this.customerId = customerId;
    }

    public BankingTransactionOwnershipException(Long transactionId, Long customerId) {
        this(Exceptions.BANKING.TRANSACTION.OWNERSHIP, transactionId, customerId);
    }

    public Long getCustomerId() {
        return customerId;
    }
}
