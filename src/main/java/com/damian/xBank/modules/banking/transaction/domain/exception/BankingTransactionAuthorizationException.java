package com.damian.xBank.modules.banking.transaction.domain.exception;

public class BankingTransactionAuthorizationException extends BankingTransactionException {
    public BankingTransactionAuthorizationException(String message, Long transactionId) {
        super(message, transactionId);
    }
}
// TODO for removal