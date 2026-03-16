package com.damian.xBank.modules.banking.transfer.outgoing.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class OutgoingTransferNotFoundException extends ApplicationException {
    public OutgoingTransferNotFoundException(Object resourceId) {
        super(ErrorCodes.BANKING_TRANSFER_NOT_FOUND, resourceId, new Object[]{});
    }

}
