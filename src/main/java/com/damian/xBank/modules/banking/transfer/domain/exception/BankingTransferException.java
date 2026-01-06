package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingTransferException extends ApplicationException {
    public BankingTransferException(String message, Object resourceId) {
        super(message, resourceId, new Object[]{});
    }

    public BankingTransferException(String message, Object resourceId, Object[] args) {
        super(message, resourceId, args);
    }

}
