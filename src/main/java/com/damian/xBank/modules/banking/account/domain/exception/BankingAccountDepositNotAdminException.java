package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountDepositNotAdminException extends BankingAccountException {
    public BankingAccountDepositNotAdminException(Long accountId, Long customerId) {
        super(ErrorCodes.BANKING_ACCOUNT_DEPOSIT_NOT_ADMIN, accountId, new Object[]{customerId});
    }
}
