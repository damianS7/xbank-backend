package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.incoming.authorize;

import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;

public record AuthorizeIncomingTransferResult(
    TransferAuthorizationStatus status,
    String authorizationId,
    String rejectionReason
) {
}