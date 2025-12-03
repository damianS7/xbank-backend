package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardInsufficientFundsException extends BankingCardException {

    public BankingCardInsufficientFundsException(Long bankingCardId) {
        this(Exceptions.BANKING.CARD.INSUFFICIENT_FUNDS, bankingCardId);
    }

    public BankingCardInsufficientFundsException(String message, Long bankingCardId) {
        super(message, bankingCardId);
    }
}
