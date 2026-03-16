package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request;

import jakarta.validation.constraints.NotNull;

public record RejectOutgoingTransferRequest(

    @NotNull(message = "Password must not be null")
    String password
) {
}
