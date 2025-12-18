package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardInsufficientFundsException extends BankingCardException {

    public BankingCardInsufficientFundsException(Long bankingCardId) {
        super(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS, bankingCardId);
    }
}
