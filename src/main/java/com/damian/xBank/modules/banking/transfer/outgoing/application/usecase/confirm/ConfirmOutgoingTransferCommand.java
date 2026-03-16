package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.confirm;

public record ConfirmOutgoingTransferCommand(
    Long transferId,
    String password
) {
}
