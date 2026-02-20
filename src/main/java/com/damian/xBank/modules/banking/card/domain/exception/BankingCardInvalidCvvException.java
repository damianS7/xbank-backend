package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class BankingCardInvalidCvvException extends BankingCardException {

    public BankingCardInvalidCvvException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_INVALID_CVV, cardId);
    }

}
