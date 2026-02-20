package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class BankingCardInvalidExpirationYearException extends BankingCardException {

    public BankingCardInvalidExpirationYearException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_INVALID_EXPIRATION_YEAR, cardId);
    }

}
