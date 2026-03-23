package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.reject;

public record RejectOutgoingTransferCommand(
    Long transferId,
    String password
) {
}
