package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardNotFoundException extends BankingCardException {
    public BankingCardNotFoundException(Long cardId) {
        this(Exceptions.BANKING.CARD.NOT_FOUND, cardId);
    }

    public BankingCardNotFoundException(String message, Long cardId) {
        super(message, cardId);
    }

    public BankingCardNotFoundException(String cardNumber) {
        this(Exceptions.BANKING.CARD.NOT_FOUND, cardNumber);
    }

    public BankingCardNotFoundException(String message, String cardNumber) {
        super(message, cardNumber);
    }
}
