package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardInsufficientFundsException extends BankingCardException {

    public BankingCardInsufficientFundsException(Long bankingCardId) {
        super(Exceptions.BANKING_CARD_INSUFFICIENT_FUNDS, bankingCardId);
    }
}
