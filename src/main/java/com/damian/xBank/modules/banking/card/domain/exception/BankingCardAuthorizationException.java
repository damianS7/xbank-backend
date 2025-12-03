package com.damian.xBank.modules.banking.card.domain.exception;


public class BankingCardAuthorizationException extends BankingCardException {
    private Long customerId;

    public BankingCardAuthorizationException(String message, Long cardId, Long customerId) {
        super(message, cardId);
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
