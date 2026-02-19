package com.damian.xBank.modules.banking.transfer.domain.model;

import java.util.Set;

public enum BankingTransferType {

    INTERNAL,
    EXTERNAL;

    private Set<BankingTransferType> allowedTransitions;

    static {
        INTERNAL.allowedTransitions = Set.of();
        EXTERNAL.allowedTransitions = Set.of();
    }

    public boolean canTransitionTo(BankingTransferType newStatus) {
        return allowedTransitions.contains(newStatus);
    }
}
