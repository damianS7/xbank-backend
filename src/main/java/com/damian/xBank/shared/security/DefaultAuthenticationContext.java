package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationContext implements AuthenticationContext {

    public DefaultAuthenticationContext() {
    }

    public User getCurrentUser() {
        return getUserPrincipal().getUser();
    }

    public UserPrincipal getUserPrincipal() {
        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
