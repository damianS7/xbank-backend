package com.damian.xBank.modules.user.token.domain.factory;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.model.UserTokenType;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserTokenFactory {

    public UserToken verificationToken(User user) {
        return UserToken.create(user, UserTokenType.ACCOUNT_VERIFICATION);
    }

    public UserToken passwordToken(User user) {
        return UserToken.create(user, UserTokenType.RESET_PASSWORD);
    }
}