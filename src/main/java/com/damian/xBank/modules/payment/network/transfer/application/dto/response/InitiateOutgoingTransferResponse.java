package com.damian.xBank.modules.payment.network.transfer.application.dto.response;

import com.damian.xBank.modules.payment.network.transfer.domain.IncomingTransferAuthorizationStatus;
import jakarta.validation.constraints.NotBlank;

public record InitiateOutgoingTransferResponse(

    @NotBlank
    String authorizationId,

    IncomingTransferAuthorizationStatus status,

    String rejectionReason
) {
}
