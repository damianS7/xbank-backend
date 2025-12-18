package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardStatusTransitionException extends BankingCardException {

    public BankingCardStatusTransitionException(Long cardId) {
        super(ErrorCodes.BANKING_CARD_INVALID_TRANSITION_STATUS, cardId);
    }

    public BankingCardStatusTransitionException(Long cardId, Object[] args) {
        super(ErrorCodes.BANKING_CARD_INVALID_TRANSITION_STATUS, cardId, args);
        // TODO review this related to BankingCardStatusTestFailed
        //String.format(Exceptions.BANKING.CARD.INVALID_TRANSITION_STATUS, this, newStatus)
    }

}
