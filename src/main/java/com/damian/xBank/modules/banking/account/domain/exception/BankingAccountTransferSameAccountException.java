package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountTransferSameAccountException extends BankingAccountTransferException {
    public BankingAccountTransferSameAccountException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }

    public BankingAccountTransferSameAccountException(Long bankingAccountId) {
        this(Exceptions.BANKING.ACCOUNT.SAME_DESTINATION, bankingAccountId);
    }
}
