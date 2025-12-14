package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

// TODO add to exception handler
public class BankingAccountStatusTransitionException extends BankingAccountException {

    public BankingAccountStatusTransitionException(Long accountId) {
        this(Exceptions.BANKING.CARD.INVALID_TRANSITION_STATUS, accountId);
    }

    public BankingAccountStatusTransitionException(String message, Long accountId) {
        super(message, accountId);
    }

}
