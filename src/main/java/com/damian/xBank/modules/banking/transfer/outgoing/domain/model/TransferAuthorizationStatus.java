package com.damian.xBank.modules.banking.transfer.outgoing.domain.model;

import java.util.Set;

public enum TransferAuthorizationStatus {

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

    private Set<TransferAuthorizationStatus> allowedTransitions;

    static {
        PENDING.allowedTransitions = Set.of(REJECTED, AUTHORIZED);
        REJECTED.allowedTransitions = Set.of();
        AUTHORIZED.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(TransferAuthorizationStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
