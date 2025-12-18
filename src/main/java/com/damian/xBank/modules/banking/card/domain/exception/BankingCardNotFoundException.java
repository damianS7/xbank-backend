package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardNotFoundException extends BankingCardException {
    public BankingCardNotFoundException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_NOT_FOUND, cardId);
    }

    public BankingCardNotFoundException(String cardNumber) {
        super(ErrorCodes.BANKING_CARD_NOT_FOUND, cardNumber);
    }

}
