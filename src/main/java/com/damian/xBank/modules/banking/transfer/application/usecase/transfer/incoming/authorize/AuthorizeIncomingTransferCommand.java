package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.incoming.authorize;

import java.math.BigDecimal;

public record AuthorizeIncomingTransferCommand(
    String toIban,
    BigDecimal amount,
    String currency
) {
}
