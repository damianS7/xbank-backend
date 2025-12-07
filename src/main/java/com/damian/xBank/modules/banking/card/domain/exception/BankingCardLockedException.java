package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardLockedException extends BankingCardException {

    public BankingCardLockedException(Long cardId) {
        this(Exceptions.BANKING.CARD.LOCKED, cardId);
    }

    public BankingCardLockedException(String message, Long cardId) {
        super(message, cardId);
    }

}
