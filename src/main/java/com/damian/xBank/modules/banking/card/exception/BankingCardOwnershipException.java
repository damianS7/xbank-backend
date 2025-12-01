package com.damian.xBank.modules.banking.card.exception;

public class BankingCardOwnershipException extends BankingCardException {
    private Long customerId;

    public BankingCardOwnershipException(String message, Long bankingCardId, Long customerId) {
        super(message, bankingCardId);
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
