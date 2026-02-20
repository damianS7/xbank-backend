package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class BankingAccountCurrencyMismatchException extends BankingAccountException {

    public BankingAccountCurrencyMismatchException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_TRANSFER_DIFFERENT_CURRENCY, bankingAccountId);
    }
}
