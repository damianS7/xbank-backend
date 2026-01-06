package com.damian.xBank.modules.banking.account.domain.model;

import java.util.Set;

public enum BankingAccountStatus {
    /**
     * Disabled by admin
     * <p>
     * From this state transitions are not allowed.
     */
    CLOSED,

    /**
     * Account suspended by admin or system.
     * <p>
     * From this state transitions are not allowed.
     */
    SUSPENDED,

    /**
     * Activated account.
     * <p>
     * From this state transitions to CLOSE or SUSPENDED are allowed.
     */
    ACTIVE;

    private Set<BankingAccountStatus> allowedTransitions;

    static {
        CLOSED.allowedTransitions = Set.of();
        SUSPENDED.allowedTransitions = Set.of(ACTIVE);

        ACTIVE.allowedTransitions = Set.of(
                CLOSED,
                SUSPENDED
        );
    }

    public boolean canTransitionTo(BankingAccountStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}