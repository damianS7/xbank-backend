package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.domain.exception.ApplicationException;
import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class BankingTransferAuthorizationFailedException extends ApplicationException {
    public BankingTransferAuthorizationFailedException(Object resourceId, String reason) {
        super(ErrorCodes.TRANSFER_AUTHORIZATION_REJECTED, resourceId, new Object[]{reason});
    }

}
