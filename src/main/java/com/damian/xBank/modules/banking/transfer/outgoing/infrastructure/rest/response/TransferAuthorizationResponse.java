package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.response;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;

public record TransferAuthorizationResponse(
    String authorizationId,
    TransferAuthorizationStatus status,
    String rejectionReason
) {
}