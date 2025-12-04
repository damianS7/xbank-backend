package com.damian.xBank.modules.banking.account.domain.exception;

public class BankingAccountTransferSameAccountException extends BankingAccountException {
    private Long toBankingAccountId;

    public BankingAccountTransferSameAccountException(
            String message,
            Long fromBankingAccountId,
            Long toBankingAccountId
    ) {
        this(message, fromBankingAccountId);
        this.toBankingAccountId = toBankingAccountId;
    }

    public BankingAccountTransferSameAccountException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }

    public Long getToBankingAccountId() {
        return toBankingAccountId;
    }
}
