package com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response;

import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;

public record IncomingTransferAuthorizationResponse(
    TransferAuthorizationStatus status,
    String authorizationId,
    String rejectionReason
) {
}