package com.damian.xBank.modules.banking.transfer.incoming.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class IncomingTransferException extends ApplicationException {
    public IncomingTransferException(String message, Object resourceId) {
        super(message, resourceId, new Object[]{});
    }

    public IncomingTransferException(String message, Object resourceId, Object[] args) {
        super(message, resourceId, args);
    }

}
