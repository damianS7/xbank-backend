package com.damian.xBank.modules.banking.account.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingAccountException extends ApplicationException {
    private Long bankingAccountId;
    private String accountNumber;

    public BankingAccountException(String message, Long bankingAccountId) {
        super(message);
        this.bankingAccountId = bankingAccountId;
    }

    public BankingAccountException(String message, String bankingAccountNumber) {
        super(message);
        this.accountNumber = bankingAccountNumber;
    }

    public String getBankingAccountId() {
        if (bankingAccountId != null) {
            return bankingAccountId.toString();
        }

        return accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
