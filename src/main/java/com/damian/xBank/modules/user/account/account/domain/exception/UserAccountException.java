package com.damian.xBank.modules.user.account.account.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class UserAccountException extends ApplicationException {
    private final Long accountId;
    private final String email;

    public UserAccountException(String message, String email) {
        super(message);
        this.email = email;
        this.accountId = null;
    }

    public UserAccountException(String message, Long accountId) {
        super(message);
        this.accountId = accountId;
        this.email = null;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountId() {
        if (email != null) {
            return email;
        }

        if (accountId != null) {
            return accountId.toString();
        }

        return "unknown";
    }
}
