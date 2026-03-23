package com.damian.xBank.modules.banking.transfer.outgoing.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class OutgoingTransferException extends ApplicationException {
    public OutgoingTransferException(String message, Object resourceId) {
        super(message, resourceId, new Object[]{});
    }

    public OutgoingTransferException(String message, Object resourceId, Object[] args) {
        super(message, resourceId, args);
    }

}
