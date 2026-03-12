package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.user.user.domain.model.User;

public class BankingAccountTestFactory {
    public static BankingAccount defaultAccount(User owner) {
        return new BankingAccountTestBuilder()
            .withId(1L)
            .withOwner(owner)
            .build();
    }
}