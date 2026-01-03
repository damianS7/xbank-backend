package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.user.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultPasswordValidator implements PasswordValidator {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public DefaultPasswordValidator(
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void validatePassword(User user, String rawPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserAccountInvalidPasswordConfirmationException(user.getId());
        }
    }

    public void validatePassword(UserPrincipal userPrincipal, String rawPassword) {
        validatePassword(userPrincipal.getUser(), rawPassword);
    }
}
