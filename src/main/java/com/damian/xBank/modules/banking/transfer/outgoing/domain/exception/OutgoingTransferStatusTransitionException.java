package com.damian.xBank.modules.banking.transfer.outgoing.domain.exception;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class OutgoingTransferStatusTransitionException extends BankingCardException {

    public OutgoingTransferStatusTransitionException(
        Long transferId, String fromStatus, String toStatus
    ) {
        this(transferId, new Object[]{fromStatus, toStatus});
    }

    public OutgoingTransferStatusTransitionException(Long transferId, Object[] args) {
        super(ErrorCodes.BANKING_TRANSFER_INVALID_TRANSITION_STATUS, transferId, args);
    }

}
