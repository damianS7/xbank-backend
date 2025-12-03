package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingTransactionException extends ApplicationException {
    private Long id;

    public BankingTransactionException(String message, Long transactionId) {
        super(message);
        this.id = transactionId;
    }

    public Long getId() {
        return id;
    }
}
