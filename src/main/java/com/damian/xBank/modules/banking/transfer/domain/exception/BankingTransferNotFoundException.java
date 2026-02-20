package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.domain.exception.ApplicationException;
import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class BankingTransferNotFoundException extends ApplicationException {
    public BankingTransferNotFoundException(Object resourceId) {
        super(ErrorCodes.BANKING_TRANSFER_NOT_FOUND, resourceId, new Object[]{});
    }

}
