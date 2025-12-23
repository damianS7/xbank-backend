package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountInsufficientFundsException extends BankingAccountException {

    public BankingAccountInsufficientFundsException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_FUNDS, bankingAccountId);
    }
}
