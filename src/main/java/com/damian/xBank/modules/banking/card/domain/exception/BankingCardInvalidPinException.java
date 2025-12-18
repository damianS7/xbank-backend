package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardInvalidPinException extends BankingCardException {

    public BankingCardInvalidPinException(Long cardId) {
        super(Exceptions.BANKING_CARD_INVALID_PIN, cardId);
    }

}
