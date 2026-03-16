package com.damian.xBank.modules.banking.transfer.outgoing.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class OutgoingTransferNotOwnerException extends OutgoingTransferException {

    public OutgoingTransferNotOwnerException(Long transferId, Long customerId) {
        super(ErrorCodes.BANKING_TRANSFER_NOT_OWNER, transferId, new Object[]{customerId});
    }
}
