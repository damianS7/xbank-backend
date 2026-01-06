package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

import java.math.BigDecimal;

public class BankingAccountInsufficientFundsException extends BankingAccountException {

    public BankingAccountInsufficientFundsException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_FUNDS, bankingAccountId);
    }

    public BankingAccountInsufficientFundsException(
            Long bankingAccountId,
            BigDecimal accountBalance,
            BigDecimal amount
    ) {
        super(ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_FUNDS, bankingAccountId, new Object[]{accountBalance, amount});
    }
}
