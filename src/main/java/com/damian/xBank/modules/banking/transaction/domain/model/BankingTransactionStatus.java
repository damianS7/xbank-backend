package com.damian.xBank.modules.banking.transaction.domain.model;

import java.util.Set;

public enum BankingTransactionStatus {
    FAILED(Set.of()), // From FAILED no transitions are allowed
    REJECTED(Set.of()), // From REJECTED no transitions are allowed
    COMPLETED(Set.of()), // From COMPLETED no transitions are allowed
    PENDING(Set.of(
            FAILED,
            REJECTED,
            COMPLETED
    )); // From PENDING transitions to FAILED, REJECTED and COMPLETED are allowed

    private final Set<BankingTransactionStatus> allowedTransitions;

    BankingTransactionStatus(Set<BankingTransactionStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(BankingTransactionStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}