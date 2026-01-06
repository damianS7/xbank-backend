package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardStatusTransitionException extends BankingCardException {

    public BankingCardStatusTransitionException(Long cardId, String fromStatus, String toStatus) {
        this(cardId, new Object[]{fromStatus, toStatus});
    }

    public BankingCardStatusTransitionException(Long cardId, Object[] args) {
        super(ErrorCodes.BANKING_CARD_INVALID_TRANSITION_STATUS, cardId, args);
    }

}
