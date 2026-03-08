package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.reject;

public record RejectOutgoingTransferCommand(
    Long transferId,
    String password
) {
}
