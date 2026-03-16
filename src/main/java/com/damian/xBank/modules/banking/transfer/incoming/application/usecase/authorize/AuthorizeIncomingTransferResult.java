package com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;

public record AuthorizeIncomingTransferResult(
    TransferAuthorizationStatus status,
    String authorizationId,
    String rejectionReason
) {
}