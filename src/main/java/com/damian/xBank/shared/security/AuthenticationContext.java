package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.customer.domain.entity.Customer;

public interface AuthenticationContext {
    Customer getCurrentCustomer();

    User getCurrentUser();

    User getUserPrincipal();

}
