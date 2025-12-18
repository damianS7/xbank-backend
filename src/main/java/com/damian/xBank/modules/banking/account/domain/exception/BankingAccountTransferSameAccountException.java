package com.damian.xBank.modules.banking.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountTransferSameAccountException extends BankingAccountTransferException {
    public BankingAccountTransferSameAccountException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_SAME_DESTINATION, bankingAccountId);
    }
}
