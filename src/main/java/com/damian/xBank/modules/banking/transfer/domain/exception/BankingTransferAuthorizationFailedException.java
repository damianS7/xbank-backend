package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransferAuthorizationFailedException extends ApplicationException {
    public BankingTransferAuthorizationFailedException(Object resourceId, String reason) {
        super(ErrorCodes.TRANSFER_AUTHORIZATION_REJECTED, resourceId, new Object[]{reason});
    }

}
