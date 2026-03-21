package com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;

public record CompleteIncomingTransferRequest(
    @NotBlank
    String authorizationId
) {
}
