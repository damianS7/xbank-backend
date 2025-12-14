package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

// TODO add to exception handler
public class BankingCardStatusTransitionException extends BankingCardException {

    public BankingCardStatusTransitionException(Long cardId) {
        this(Exceptions.BANKING.CARD.INVALID_TRANSITION_STATUS, cardId);
    }

    public BankingCardStatusTransitionException(String message, Long cardId) {
        super(message, cardId);
    }

}
