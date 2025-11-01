package com.damian.xBank.modules.banking.card.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingCardException extends ApplicationException {
    private Long cardId;
    private String cardNumber;

    public BankingCardException(String message, Long cardId) {
        super(message);
        this.cardId = cardId;
    }

    public BankingCardException(String message, String cardNumber) {
        super(message);
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getBankingCardId() {
        if (cardId != null) {
            return cardId.toString();
        }

        return cardNumber;
    }
}
