package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

import java.math.BigDecimal;

public class BankingAccountInsufficientReservedFundsException extends BankingAccountException {

    public BankingAccountInsufficientReservedFundsException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_RESERVED_FUNDS, bankingAccountId);
    }

    public BankingAccountInsufficientReservedFundsException(
        Long bankingAccountId,
        BigDecimal accountBalance,
        BigDecimal amount
    ) {
        super(
            ErrorCodes.BANKING_ACCOUNT_INSUFFICIENT_RESERVED_FUNDS,
            bankingAccountId,
            new Object[]{accountBalance, amount}
        );
    }
}
