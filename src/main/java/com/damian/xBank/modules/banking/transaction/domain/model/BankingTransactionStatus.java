package com.damian.xBank.modules.banking.transaction.domain.model;

import java.util.Set;

public enum BankingTransactionStatus {
    REJECTED(Set.of()),
    FAILED(Set.of()),
    COMPLETED(Set.of()),
    PENDING(Set.of(COMPLETED, FAILED, REJECTED));

    private final Set<BankingTransactionStatus> allowedTransitions;

    BankingTransactionStatus(Set<BankingTransactionStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(BankingTransactionStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}