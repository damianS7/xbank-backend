package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransferSameAccountException extends BankingTransferException {
    public BankingTransferSameAccountException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_TRANSFER_SAME_ACCOUNT, bankingAccountId);
    }
}
