package com.damian.whatsapp.shared.util;

import com.damian.whatsapp.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserPrincipal;
import com.damian.whatsapp.shared.exception.Exceptions;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthHelper {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static void validatePassword(User user, String rawPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserAccountInvalidPasswordConfirmationException(
                    Exceptions.ACCOUNT.INVALID_PASSWORD,
                    user.getId()
            );
        }
    }

    public static User getLoggedUser() {
        return AuthHelper.getUserPrincipal().getUser();
    }

    public static UserPrincipal getUserPrincipal() {
        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static boolean isAdmin(User user) {
        return user.getRole().equals(UserRole.ADMIN);
    }

}
