package com.damian.xBank.modules.banking.transfer.incoming.application.usecase.complete;

import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransferStatus;

public record CompleteIncomingTransferResult(
    IncomingTransferStatus status
) {

    public static CompleteIncomingTransferResult from(final IncomingTransfer incomingTransfer) {
        return new CompleteIncomingTransferResult(incomingTransfer.getStatus());
    }
}