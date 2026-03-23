package com.damian.xBank.modules.banking.transfer.incoming.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class IncomingTransferNotOwnerException extends IncomingTransferException {

    public IncomingTransferNotOwnerException(Long transferId, Long customerId) {
        super(ErrorCodes.BANKING_TRANSFER_NOT_OWNER, transferId, new Object[]{customerId});
    }
}
