package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create;

import java.math.BigDecimal;

public record CreateOutgoingTransferCommand(
    Long fromAccountId,
    String toAccountNumber,
    String description,
    BigDecimal amount
) {
}
