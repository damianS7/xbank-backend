package com.damian.xBank.modules.banking.transfer.incoming.application.usecase.complete;

public record CompleteIncomingTransferCommand(
    String authorizationId
) {
}
