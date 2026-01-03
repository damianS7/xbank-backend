package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.account.account.domain.model.User;

public interface PasswordValidator {

    void validatePassword(User user, String rawPassword);

    void validatePassword(UserPrincipal user, String rawPassword);
}
