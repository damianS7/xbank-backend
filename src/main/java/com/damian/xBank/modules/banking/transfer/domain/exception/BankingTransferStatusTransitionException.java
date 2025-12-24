package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransferStatusTransitionException extends BankingCardException {

    public BankingTransferStatusTransitionException(String fromStatus, String toStatus) {
        this(new Object[]{fromStatus, toStatus});
    }

    public BankingTransferStatusTransitionException(Object[] args) {
        super(ErrorCodes.BANKING_TRANSFER_INVALID_TRANSITION_STATUS, args);
    }

}
