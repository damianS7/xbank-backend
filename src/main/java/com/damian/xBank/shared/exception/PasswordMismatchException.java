package com.damian.xBank.shared.exception;

// TODO no usage?
public class PasswordMismatchException extends ApplicationException {

    public PasswordMismatchException() {
        super(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD, "", new Object[]{});
    }
}
