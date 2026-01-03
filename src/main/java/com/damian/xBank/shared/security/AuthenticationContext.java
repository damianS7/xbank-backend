package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.account.account.domain.model.User;

public interface AuthenticationContext {
    User getCurrentUser();

    UserPrincipal getUserPrincipal();

}
