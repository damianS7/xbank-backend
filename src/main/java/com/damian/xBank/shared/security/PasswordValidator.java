package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;

public interface PasswordValidator {

    void validatePassword(UserAccount user, String rawPassword);

    void validatePassword(User user, String rawPassword);

    void validatePassword(Customer customer, String rawPassword);

}
