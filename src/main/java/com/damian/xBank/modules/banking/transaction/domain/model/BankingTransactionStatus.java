package com.damian.xBank.modules.banking.transaction.domain.model;

import java.util.Set;

public enum BankingTransactionStatus {
    DECLINED(Set.of()), // From DECLINED no transitions are allowed
    CAPTURED(Set.of()), // From CAPTURED no transitions are allowed
    COMPLETED(Set.of()),
    AUTHORIZED(Set.of(CAPTURED, DECLINED)), // From AUTHORIZED no transitions are allowed
    PENDING(Set.of(
            AUTHORIZED,
            DECLINED
    )); // From PENDING transitions to AUTHORIZED, DECLINED and CAPTURED are allowed

    private final Set<BankingTransactionStatus> allowedTransitions;

    BankingTransactionStatus(Set<BankingTransactionStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(BankingTransactionStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}