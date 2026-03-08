package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountSuspendedException extends BankingAccountException {

    public BankingAccountSuspendedException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_SUSPENDED, bankingAccountId);
    }

}
