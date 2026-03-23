package com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize;

import java.math.BigDecimal;

public record AuthorizeIncomingTransferCommand(
    String authorizationId,
    String fromIban,
    String toIban,
    BigDecimal amount,
    String currency,
    String reference
) {
}
