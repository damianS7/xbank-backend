package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardInvalidPinException extends BankingCardException {

    public BankingCardInvalidPinException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_INVALID_PIN, cardId);
    }

}
