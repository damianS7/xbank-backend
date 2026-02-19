package com.damian.xBank.modules.payment.network.transfer.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OutgoingTransferFailureRequest(
    @NotBlank
    String authorizationId,

    @NotBlank
    String failure
) {
}
