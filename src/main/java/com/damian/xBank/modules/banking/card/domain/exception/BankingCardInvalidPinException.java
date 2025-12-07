package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardInvalidPinException extends BankingCardException {

    public BankingCardInvalidPinException(Long cardId) {
        this(Exceptions.BANKING.CARD.INVALID_PIN, cardId);
    }

    public BankingCardInvalidPinException(String message, Long cardId) {
        super(message, cardId);
    }

}
