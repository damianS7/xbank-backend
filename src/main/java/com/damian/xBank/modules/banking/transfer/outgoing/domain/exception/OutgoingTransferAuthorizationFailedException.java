package com.damian.xBank.modules.banking.transfer.outgoing.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class OutgoingTransferAuthorizationFailedException extends ApplicationException {
    public OutgoingTransferAuthorizationFailedException(Object resourceId, String reason) {
        super(ErrorCodes.TRANSFER_AUTHORIZATION_REJECTED, resourceId, new Object[]{reason});
    }

}
