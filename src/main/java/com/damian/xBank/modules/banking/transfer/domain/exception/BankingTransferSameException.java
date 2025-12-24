package com.damian.xBank.modules.banking.transfer.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingTransferSameException extends BankingTransferException {
    public BankingTransferSameException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_TRANSFER_SAME_DESTINATION, bankingAccountId);
    }
}
