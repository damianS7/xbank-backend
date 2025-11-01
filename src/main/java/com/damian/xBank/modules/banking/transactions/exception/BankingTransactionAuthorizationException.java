package com.damian.xBank.modules.banking.transactions.exception;

public class BankingTransactionAuthorizationException extends BankingTransactionException {
    public BankingTransactionAuthorizationException(String message, Long transactionId) {
        super(message, transactionId);
    }
}
