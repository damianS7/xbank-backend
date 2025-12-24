package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransferNotOwnerException extends BankingTransferException {

    public BankingTransferNotOwnerException(Long transferId, Long customerId) {
        super(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER, transferId, new Object[]{customerId}); // TODO add message
    }
}
