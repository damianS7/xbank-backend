package com.damian.xBank.modules.payment.network.transfer.domain;

import java.util.Set;

public enum IncomingTransferAuthorizationStatus {

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

    private Set<IncomingTransferAuthorizationStatus> allowedTransitions;

    static {
        REJECTED.allowedTransitions = Set.of();

        AUTHORIZED.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(IncomingTransferAuthorizationStatus newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
