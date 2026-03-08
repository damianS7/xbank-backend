package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.authorize;

public record AuthorizeOutgoingTransferCommand(
    Long transferId
) {
}
