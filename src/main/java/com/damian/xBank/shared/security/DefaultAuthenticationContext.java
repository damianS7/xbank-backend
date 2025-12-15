package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationContext implements AuthenticationContext {

    public DefaultAuthenticationContext() {
    }

    public Customer getCurrentCustomer() {
        return getUserPrincipal().getCustomer();
    }

    public User getCurrentUser() {
        return getUserPrincipal();
    }

    public User getUserPrincipal() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
