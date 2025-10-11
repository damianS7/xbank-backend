package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.enums.UserRole;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.domain.UserPrincipal;
import com.damian.xBank.shared.exception.Exceptions;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthHelper {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static void validatePassword(UserAccount user, String rawPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserAccountInvalidPasswordConfirmationException(
                    Exceptions.ACCOUNT.INVALID_PASSWORD,
                    user.getId()
            );
        }
    }

    public static Customer getLoggedCustomer() {
        return AuthHelper.getUserPrincipal().getAccount().getCustomer();
    }

    public static UserAccount getLoggedUser() {
        return AuthHelper.getUserPrincipal().getAccount();
    }

    public static UserPrincipal getUserPrincipal() {
        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static boolean isAdmin(UserAccount user) {
        return user.getRole().equals(UserRole.ADMIN);
    }

}
