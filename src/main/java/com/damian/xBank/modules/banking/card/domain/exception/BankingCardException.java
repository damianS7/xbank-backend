package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingCardException extends ApplicationException {
    public BankingCardException(String message, Object resourceId) {
        super(message, resourceId, new Object[]{resourceId});
    }

    public BankingCardException(String message, Object resourceId, Object[] args) {
        super(message, resourceId, args);
    }
}
