package com.damian.xBank.modules.user.token.domain.factory;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import org.springframework.stereotype.Component;

@Component
public class UserTokenFactory {

    public UserToken verificationToken() {
        UserToken token = new UserToken();
        token.generateVerificationToken();
        return token;
    }

    public UserToken passwordToken() {
        UserToken token = new UserToken();
        token.generateResetPasswordToken();
        return token;
    }
}