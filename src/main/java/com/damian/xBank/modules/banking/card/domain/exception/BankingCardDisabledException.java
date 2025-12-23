package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardDisabledException extends BankingCardException {

    public BankingCardDisabledException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_DISABLED, cardId);
    }

}
