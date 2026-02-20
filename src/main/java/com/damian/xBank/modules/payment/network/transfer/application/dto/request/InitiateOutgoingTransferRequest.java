package com.damian.xBank.modules.payment.network.transfer.application.dto.request;

import jakarta.validation.constraints.Positive;

public record InitiateOutgoingTransferRequest(
    @Positive
    Long transferId
) {
}
