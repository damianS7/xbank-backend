package com.damian.xBank.modules.banking.transfer.domain.model;

import java.util.Set;

public enum BankingTransferStatus {

    /**
     * Emitted but not activated by user.
     * <p>
     * From this state transitions to REJECTED, CONFIRMED are allowed.
     */
    PENDING,

    /**
     * Transfer rejected
     * <p>
     * From this state no transitions are not allowed.
     */
    REJECTED,

    /**
     * Transfer confirmed.
     * <p>
     * From this state no transitions are allowed.
     */
    AUTHORIZED,
    CONFIRMED,
    COMPLETED;

    private Set<BankingTransferStatus> allowedTransitions;

    static {
        REJECTED.allowedTransitions = Set.of();
        PENDING.allowedTransitions = Set.of(
            CONFIRMED,
            REJECTED
        );
        CONFIRMED.allowedTransitions = Set.of(AUTHORIZED, REJECTED);
        AUTHORIZED.allowedTransitions = Set.of(COMPLETED);
        COMPLETED.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(BankingTransferStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
