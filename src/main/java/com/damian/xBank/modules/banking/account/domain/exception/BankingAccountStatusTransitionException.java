package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountStatusTransitionException extends BankingAccountException {

    public BankingAccountStatusTransitionException(String fromStatus, String toStatus) {
        this(new Object[]{fromStatus, toStatus});
    }

    public BankingAccountStatusTransitionException(Object[] args) {
        super(ErrorCodes.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS, args);
    }

}
