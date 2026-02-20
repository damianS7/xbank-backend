package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class BankingAccountDepositException extends BankingAccountException {
    public BankingAccountDepositException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_FAILED_DEPOSIT, bankingAccountId);
    }
}
