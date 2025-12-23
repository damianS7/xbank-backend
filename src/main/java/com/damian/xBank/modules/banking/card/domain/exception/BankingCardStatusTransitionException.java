package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardStatusTransitionException extends BankingCardException {

    public BankingCardStatusTransitionException(String fromStatus, String toStatus) {
        this(new Object[]{fromStatus, toStatus});
    }

    public BankingCardStatusTransitionException(Object[] args) {
        super(ErrorCodes.BANKING_CARD_INVALID_TRANSITION_STATUS, args);
    }

}
