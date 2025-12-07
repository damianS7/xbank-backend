package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardDisabledException extends BankingCardException {

    public BankingCardDisabledException(Long cardId) {
        this(Exceptions.BANKING.CARD.DISABLED, cardId);
    }

    public BankingCardDisabledException(String message, Long cardId) {
        super(message, cardId);
    }

}
