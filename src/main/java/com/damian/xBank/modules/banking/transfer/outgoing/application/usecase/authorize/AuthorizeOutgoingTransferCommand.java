package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize;

public record AuthorizeOutgoingTransferCommand(
    Long transferId
) {
}
