package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.confirm;

public record ConfirmOutgoingTransferCommand(
    Long transferId,
    String password
) {
}
