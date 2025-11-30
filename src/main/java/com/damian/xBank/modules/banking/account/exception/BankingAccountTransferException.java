package com.damian.xBank.modules.banking.account.exception;

public class BankingAccountTransferException extends BankingAccountException {
    private Long toBankingAccountId;

    public BankingAccountTransferException(String message, Long fromBankingAccountId, Long toBankingAccountId) {
        this(message, fromBankingAccountId);
        this.toBankingAccountId = toBankingAccountId;
    }

    public BankingAccountTransferException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }

    public Long getToBankingAccountId() {
        return toBankingAccountId;
    }
}
