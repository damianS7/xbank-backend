package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardExpiredException extends BankingCardException {

    public BankingCardExpiredException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_EXPIRED, cardId);
    }

}
