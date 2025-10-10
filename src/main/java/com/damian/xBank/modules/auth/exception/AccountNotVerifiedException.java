package com.damian.whatsapp.modules.auth.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class AccountNotVerifiedException extends ApplicationException {
    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
