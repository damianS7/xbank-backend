package com.damian.xBank.modules.banking.transfer.outgoing.domain.model;

import java.util.Set;

public enum OutgoingTransferType {

    INTERNAL,
    EXTERNAL;

    private Set<OutgoingTransferType> allowedTransitions;

    static {
        INTERNAL.allowedTransitions = Set.of();
        EXTERNAL.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(OutgoingTransferType newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
