package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardDisabledException extends BankingCardException {

    public BankingCardDisabledException(Long cardId) {
        super(Exceptions.BANKING_CARD_DISABLED, cardId);
    }

}
