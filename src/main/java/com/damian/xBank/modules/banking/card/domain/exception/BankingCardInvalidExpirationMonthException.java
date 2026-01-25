package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardInvalidExpirationMonthException extends BankingCardException {

    public BankingCardInvalidExpirationMonthException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_INVALID_EXPIRATION_MONTH, cardId);
    }

}
