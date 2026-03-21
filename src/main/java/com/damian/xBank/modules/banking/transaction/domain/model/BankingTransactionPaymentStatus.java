package com.damian.xBank.modules.banking.transaction.domain.model;

import java.util.Set;

public enum BankingTransactionPaymentStatus {
    FAILED(Set.of()),
    CAPTURED(Set.of()),
    AUTHORIZED(Set.of(CAPTURED)),
    PENDING(Set.of(FAILED, AUTHORIZED));

    private final Set<BankingTransactionPaymentStatus> allowedTransitions;

    BankingTransactionPaymentStatus(Set<BankingTransactionPaymentStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(BankingTransactionPaymentStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}