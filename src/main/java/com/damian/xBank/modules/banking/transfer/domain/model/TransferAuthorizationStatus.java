package com.damian.xBank.modules.banking.transfer.domain.model;

import java.util.Set;

public enum TransferAuthorizationStatus {

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
        REJECTED.allowedTransitions = Set.of();

        AUTHORIZED.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(TransferAuthorizationStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
