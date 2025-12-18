package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountTransferSameAccountException extends BankingAccountTransferException {
    public BankingAccountTransferSameAccountException(Long bankingAccountId) {
        super(Exceptions.BANKING_ACCOUNT_SAME_DESTINATION, bankingAccountId);
    }
}
