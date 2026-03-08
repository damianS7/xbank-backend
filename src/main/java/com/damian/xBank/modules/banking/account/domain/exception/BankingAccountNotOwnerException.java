package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountNotOwnerException extends BankingAccountException {

    public BankingAccountNotOwnerException(Long bankingAccountId, Long customerId) {
        super(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER, bankingAccountId, new Object[]{customerId});
    }
}
