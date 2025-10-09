package com.damian.xBank.modules.customer.profile.exception;

import com.damian.xBank.modules.auth.exception.AuthorizationException;

public class ProfileAuthorizationException extends AuthorizationException {
    public ProfileAuthorizationException(String message) {
        super(message);
    }
}
