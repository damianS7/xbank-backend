package com.damian.xBank.modules.banking.transaction.domain.model;

import java.util.Set;

public enum BankingTransactionStatus {
    FAILED(Set.of()), // From FAILED no transitions are allowed
    COMPLETED(Set.of()),
    PENDING(Set.of(
        COMPLETED,
        FAILED
    )); // From PENDING transitions to FAILED and COMPLETED are allowed

    private final Set<BankingTransactionStatus> allowedTransitions;

    BankingTransactionStatus(Set<BankingTransactionStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(BankingTransactionStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}