package com.damian.xBank.modules.banking.transaction.domain.model;

import java.util.Set;

public enum BankingTransactionStatus {
    DECLINED(Set.of()), // From DECLINED no transitions are allowed
    COMPLETED(Set.of()),
    PENDING(Set.of(
            COMPLETED,
            DECLINED
    )); // From PENDING transitions to DECLINED and COMPLETED are allowed

    private final Set<BankingTransactionStatus> allowedTransitions;

    BankingTransactionStatus(Set<BankingTransactionStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(BankingTransactionStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}