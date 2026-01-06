package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.user.domain.model.User;

public interface AuthenticationContext {
    User getCurrentUser();

    UserPrincipal getUserPrincipal();

}
