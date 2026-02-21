package com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request;

import jakarta.validation.constraints.Positive;

public record InitiateOutgoingTransferRequest(
    @Positive
    Long transferId
) {
}
