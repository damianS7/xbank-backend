package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountStatusTransitionException extends BankingAccountException {

    public BankingAccountStatusTransitionException(Long accountId) {
        super(ErrorCodes.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS, accountId);
    }

    public BankingAccountStatusTransitionException(Long accountId, Object[] args) {
        super(ErrorCodes.BANKING_ACCOUNT_INVALID_TRANSITION_STATUS, accountId, args);
    }

}
