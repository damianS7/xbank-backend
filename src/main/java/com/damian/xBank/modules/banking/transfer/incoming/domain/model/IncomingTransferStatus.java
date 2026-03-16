package com.damian.xBank.modules.banking.transfer.incoming.domain.model;

import java.util.Set;

public enum IncomingTransferStatus {

    /**
     * Transfer is created and pending confirmation.
     */
    PENDING, // TODO remove?

    /**
     * Transfer fails. Funds are released.
     */
    FAILED, // TODO remove?

    /**
     * Transfer is authorized by the bank.
     */
    AUTHORIZED,

    /**
     * Transfer is completed. Funds are moved.
     */
    COMPLETED;

    private Set<IncomingTransferStatus> allowedTransitions;

    static {
        PENDING.allowedTransitions = Set.of(AUTHORIZED, FAILED);
        FAILED.allowedTransitions = Set.of();
        AUTHORIZED.allowedTransitions = Set.of(COMPLETED, FAILED);
        COMPLETED.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(IncomingTransferStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
