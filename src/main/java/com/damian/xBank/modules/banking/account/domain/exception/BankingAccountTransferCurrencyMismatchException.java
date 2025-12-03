package com.damian.xBank.modules.banking.account.domain.exception;

public class BankingAccountTransferCurrencyMismatchException extends BankingAccountException {
    private Long toBankingAccountId;

    public BankingAccountTransferCurrencyMismatchException(
            String message,
            Long fromBankingAccountId,
            Long toBankingAccountId
    ) {
        this(message, fromBankingAccountId);
        this.toBankingAccountId = toBankingAccountId;
    }

    public BankingAccountTransferCurrencyMismatchException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }

    public Long getToBankingAccountId() {
        return toBankingAccountId;
    }
}
