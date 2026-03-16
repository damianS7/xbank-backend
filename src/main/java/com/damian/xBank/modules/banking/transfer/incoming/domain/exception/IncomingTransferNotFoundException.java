package com.damian.xBank.modules.banking.transfer.incoming.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class IncomingTransferNotFoundException extends ApplicationException {
    public IncomingTransferNotFoundException(Object resourceId) {
        super(ErrorCodes.BANKING_TRANSFER_NOT_FOUND, resourceId, new Object[]{});
    }

}
