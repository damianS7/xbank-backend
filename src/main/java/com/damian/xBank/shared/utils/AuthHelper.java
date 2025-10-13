package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.User;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.exception.Exceptions;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthHelper {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static void validatePassword(UserAccount user, String rawPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserAccountInvalidPasswordConfirmationException(
                    Exceptions.USER.ACCOUNT.INVALID_PASSWORD,
                    user.getId()
            );
        }
    }

    public static void validatePassword(User user, String rawPassword) {
        AuthHelper.validatePassword(user.getAccount(), rawPassword);
    }

    public static void validatePassword(Customer customer, String rawPassword) {
        AuthHelper.validatePassword(customer.getAccount(), rawPassword);
    }

    public static Customer getCurrentCustomer() {
        return AuthHelper.getUserPrincipal().getCustomer();
    }

    public static User getCurrentUser() {
        return AuthHelper.getUserPrincipal();
    }

    public static User getUserPrincipal() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static boolean isAdmin(UserAccount user) {
        return user.getRole().equals(UserAccountRole.ADMIN);
    }

}
