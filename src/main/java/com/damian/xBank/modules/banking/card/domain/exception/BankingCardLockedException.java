package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardLockedException extends BankingCardException {

    public BankingCardLockedException(Long cardId) {
        super(Exceptions.BANKING_CARD_LOCKED, cardId);
    }

}
