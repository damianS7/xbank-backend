package com.damian.xBank.modules.banking.transfer.domain.enums;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardStatusTransitionException;

import java.util.Set;

public enum BankingTransferStatus {

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
    CONFIRMED,

    /**
     * Emitted but not activated by user.
     * <p>
     * From this state transitions to REJECTED, CONFIRMED are allowed.
     */
    PENDING;

    private Set<BankingTransferStatus> allowedTransitions;

    static {
        REJECTED.allowedTransitions = Set.of();

        CONFIRMED.allowedTransitions = Set.of();

        PENDING.allowedTransitions = Set.of(
                CONFIRMED,
                REJECTED
        );
    }

    public boolean canTransitionTo(BankingTransferStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }

    public void validateTransition(BankingTransferStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new BankingCardStatusTransitionException(this.name(), newStatus.name());
        }
    }
}
