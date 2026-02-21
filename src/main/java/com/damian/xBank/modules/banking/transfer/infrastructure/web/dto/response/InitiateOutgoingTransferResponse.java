package com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response;

import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;
import jakarta.validation.constraints.NotBlank;

public record InitiateOutgoingTransferResponse(

    @NotBlank
    String authorizationId,

    TransferAuthorizationStatus status,

    String rejectionReason
) {
}
