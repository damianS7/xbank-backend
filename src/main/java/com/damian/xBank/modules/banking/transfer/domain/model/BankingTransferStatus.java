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
    AUTHORIZED;

    private Set<BankingTransferStatus> allowedTransitions;

    static {
        REJECTED.allowedTransitions = Set.of();
        AUTHORIZED.allowedTransitions = Set.of();
        PENDING.allowedTransitions = Set.of(
            AUTHORIZED,
            REJECTED
        );
    }

    public boolean canTransitionTo(BankingTransferStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
