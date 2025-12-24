package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransferNotFoundException extends ApplicationException {
    public BankingTransferNotFoundException(Long transferId) {
        super(ErrorCodes.BANKING_TRANSFER_NOT_FOUND, transferId, new Object[]{});
    }

}
