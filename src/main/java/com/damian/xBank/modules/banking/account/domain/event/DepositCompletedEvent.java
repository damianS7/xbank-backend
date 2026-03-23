package com.damian.xBank.modules.banking.account.domain.event;

import java.math.BigDecimal;

public record DepositCompletedEvent(
    Long transactionId,
    Long accountId,
    String depositor,
    BigDecimal depositAmount,
    String currency
) {
}