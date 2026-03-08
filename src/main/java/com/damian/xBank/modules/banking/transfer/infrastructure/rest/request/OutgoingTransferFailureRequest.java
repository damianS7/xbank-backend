package com.damian.xBank.modules.banking.transfer.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;

public record OutgoingTransferFailureRequest(
    @NotBlank
    String authorizationId,

    @NotBlank
    String failure
) {
}
