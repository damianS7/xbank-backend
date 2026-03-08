package com.damian.xBank.modules.banking.transfer.infrastructure.rest.response;

import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;

public record TransferAuthorizationResponse(
    String authorizationId,
    TransferAuthorizationStatus status,
    String rejectionReason
) {
}