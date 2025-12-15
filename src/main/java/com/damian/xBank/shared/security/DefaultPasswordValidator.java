package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;
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

    public void validatePassword(UserAccount user, String rawPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserAccountInvalidPasswordConfirmationException(
                    Exceptions.USER.ACCOUNT.INVALID_PASSWORD,
                    user.getId()
            );
        }
    }

    public void validatePassword(User user, String rawPassword) {
        validatePassword(user.getAccount(), rawPassword);
    }

    public void validatePassword(Customer customer, String rawPassword) {
        validatePassword(customer.getAccount(), rawPassword);
    }

}
