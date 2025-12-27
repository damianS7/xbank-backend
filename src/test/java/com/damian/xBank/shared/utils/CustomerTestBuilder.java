package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;

public class CustomerTestBuilder {

    private Customer customer;
    private UserAccount userAccount;
    private Long id = 1L;
    private String email = "customer@demo.com";
    private UserAccountRole userAccountRole = UserAccountRole.CUSTOMER;

    public static CustomerTestBuilder defaultAccount() {
        CustomerTestBuilder builder = new CustomerTestBuilder();
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountStatus(UserAccountStatus.VERIFIED);
        userAccount.setEmail(builder.email);
        userAccount.setRole(builder.userAccountRole);
        //        userAccount.setPassword();

        return builder;
    }

    public CustomerTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public CustomerTestBuilder withUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public Customer build() {
        return Customer.create(userAccount)
                       .setId(id);
    }
}