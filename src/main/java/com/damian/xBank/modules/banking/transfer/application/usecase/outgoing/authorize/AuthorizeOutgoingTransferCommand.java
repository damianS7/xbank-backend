package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize;

public record AuthorizeOutgoingTransferCommand(
    Long transferId
) {
}
