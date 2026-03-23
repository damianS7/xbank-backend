package com.damian.xBank.modules.banking.transfer.incoming.domain.exception;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class IncomingTransferStatusTransitionException extends BankingCardException {

    public IncomingTransferStatusTransitionException(
        Long transferId, String fromStatus, String toStatus
    ) {
        this(transferId, new Object[]{fromStatus, toStatus});
    }

    public IncomingTransferStatusTransitionException(Long transferId, Object[] args) {
        super(ErrorCodes.BANKING_TRANSFER_INVALID_TRANSITION_STATUS, transferId, args);
    }

}
