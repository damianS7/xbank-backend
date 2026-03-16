package com.damian.xBank.modules.banking.transfer.outgoing.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class OutgoingTransferSameAccountException extends OutgoingTransferException {
    public OutgoingTransferSameAccountException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_TRANSFER_SAME_ACCOUNT, bankingAccountId);
    }
}
