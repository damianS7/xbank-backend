package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardNotFoundException extends BankingCardException {
    public BankingCardNotFoundException(Long cardId) {
        super(Exceptions.BANKING_CARD_NOT_FOUND, cardId);
    }

    public BankingCardNotFoundException(String cardNumber) {
        super(Exceptions.BANKING_CARD_NOT_FOUND, cardNumber);
    }

}
