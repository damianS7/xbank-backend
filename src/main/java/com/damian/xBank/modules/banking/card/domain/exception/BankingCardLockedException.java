package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardLockedException extends BankingCardException {

    public BankingCardLockedException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_LOCKED, cardId);
    }

}
