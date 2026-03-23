package com.damian.xBank.modules.banking.transfer.outgoing.domain.model;

import java.util.Set;

public enum OutgoingTransferStatus {

    /**
     * Transfer is created and pending confirmation.
     */
    PENDING,

    /**
     * Transfer is confirmed by the user. Hold funds and wait for bank authorization.
     */
    CONFIRMED,

    /**
     * Transfer is rejected by the user.
     */
    REJECTED,

    /**
     * Transfer fails. Funds are released.
     */
    FAILED,

    /**
     * Transfer is authorized by the bank.
     */
    AUTHORIZED,

    /**
     * Transfer is completed. Funds are moved.
     */
    COMPLETED;

    private Set<OutgoingTransferStatus> allowedTransitions;

    static {
        PENDING.allowedTransitions = Set.of(CONFIRMED, REJECTED);
        FAILED.allowedTransitions = Set.of(PENDING);
        REJECTED.allowedTransitions = Set.of();
        CONFIRMED.allowedTransitions = Set.of(AUTHORIZED, FAILED);
        AUTHORIZED.allowedTransitions = Set.of(COMPLETED, FAILED);
        COMPLETED.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(OutgoingTransferStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
