package com.damian.xBank.modules.banking.card.domain.enums;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardStatusTransitionException;
import com.damian.xBank.shared.exception.Exceptions;

import java.util.Set;

// TODO
public enum BankingCardStatus {
    DISABLED,
    EXPIRED,
    ACTIVE,
    LOCKED,
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


    /**
     * Disabled by admin
     * <p>
     * From this state transitions are not allowed.
     */
    //    DISABLED(Set.of()),

    /**
     * Card expired by time.
     * <p>
     * From this state transitions are not allowed.
     */
    //    EXPIRED(Set.of()),

    /**
     * Activated card.
     * <p>
     * From this state transitions to DISABLED or EXPIRED are allowed.
     */
    //    ACTIVE(Set.of(DISABLED, EXPIRED, LOCKED)),

    /**
     * Locked by user
     * <p>
     * From this state transitions are not allowed.
     */
    //    LOCKED(Set.of(ACTIVE, DISABLED, EXPIRED)),

    /**
     * Emitted but not activated by user.
     * <p>
     * From this state transitions to ACTIVE, DISABLED or EXPIRED are allowed.
     */
    //    PENDING_ACTIVATION(Set.of(ACTIVE, DISABLED, EXPIRED));

    //    private final Set<BankingCardStatus> allowedTransitions;

    //    BankingCardStatus(Set<BankingCardStatus> allowedTransitions) {
    //        this.allowedTransitions = allowedTransitions;
    //    }
    public boolean canTransitionTo(BankingCardStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }

    public void validateTransition(BankingCardStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new BankingCardStatusTransitionException(
                    // TODO review this
                    String.format(Exceptions.BANKING.CARD.INVALID_TRANSITION_STATUS, this, newStatus), 0L
            );
        }
    }
}
