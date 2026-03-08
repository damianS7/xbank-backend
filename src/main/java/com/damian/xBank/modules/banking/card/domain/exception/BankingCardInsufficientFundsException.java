package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

import java.math.BigDecimal;

public class BankingCardInsufficientFundsException extends BankingCardException {

    public BankingCardInsufficientFundsException(Long bankingCardId) {
        super(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS, bankingCardId);
    }

    public BankingCardInsufficientFundsException(Long bankingCardId, BigDecimal balance, BigDecimal amount) {
        super(ErrorCodes.BANKING_CARD_INSUFFICIENT_FUNDS, bankingCardId, new Object[]{balance, amount});
    }
}
