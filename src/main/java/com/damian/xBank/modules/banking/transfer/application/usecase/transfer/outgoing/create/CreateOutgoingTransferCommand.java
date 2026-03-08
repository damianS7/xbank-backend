package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.create;

import java.math.BigDecimal;

public record CreateOutgoingTransferCommand(
    Long fromAccountId,
    String toAccountNumber,
    String description,
    BigDecimal amount
) {
}
