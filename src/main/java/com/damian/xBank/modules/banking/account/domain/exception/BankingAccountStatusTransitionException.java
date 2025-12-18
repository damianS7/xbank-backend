package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountStatusTransitionException extends BankingAccountException {

    public BankingAccountStatusTransitionException(Long accountId) {
        super(Exceptions.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS, accountId);
    }

    public BankingAccountStatusTransitionException(Long accountId, Object[] args) {
        super(Exceptions.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS, accountId, args);
    }

}
