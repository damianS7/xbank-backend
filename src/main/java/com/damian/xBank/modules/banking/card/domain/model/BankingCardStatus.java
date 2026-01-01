package com.damian.xBank.modules.banking.card.domain.model;

import java.util.Set;

public enum BankingCardStatus {
    /**
     * Disabled by admin
     * <p>
     * From this state transitions are not allowed.
     */
    DISABLED,

    /**
     * Card expired by time.
     * <p>
     * From this state transitions are not allowed.
     */
    EXPIRED,

    /**
     * Activated card.
     * <p>
     * From this state transitions to DISABLED or EXPIRED are allowed.
     */
    ACTIVE,

    /**
     * Locked by user
     * <p>
     * From this state transitions are not allowed.
     */
    LOCKED,

    /**
     * Emitted but not activated by user.
     * <p>
     * From this state transitions to ACTIVE, DISABLED or EXPIRED are allowed.
     */
    PENDING_ACTIVATION;

    private Set<BankingCardStatus> allowedTransitions;

    static {
        DISABLED.allowedTransitions = Set.of();
        EXPIRED.allowedTransitions = Set.of();

        ACTIVE.allowedTransitions = Set.of(
                DISABLED,
                EXPIRED,
                LOCKED
        );

        LOCKED.allowedTransitions = Set.of(
                ACTIVE,
                DISABLED,
                EXPIRED
        );

        PENDING_ACTIVATION.allowedTransitions = Set.of(
                ACTIVE,
                DISABLED,
                EXPIRED
        );
    }

    public boolean canTransitionTo(BankingCardStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
