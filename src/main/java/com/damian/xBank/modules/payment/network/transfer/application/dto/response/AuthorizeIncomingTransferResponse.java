package com.damian.xBank.modules.payment.network.transfer.application.dto.response;

import com.damian.xBank.modules.payment.network.transfer.domain.IncomingTransferAuthorizationStatus;

public record AuthorizeIncomingTransferResponse(
    IncomingTransferAuthorizationStatus status,
    String authorizationId,
    String rejectionReason
) {
}