package com.damian.xBank.test.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.user.user.domain.model.User;

public class BankingAccountTestFactory {
    public static BankingAccountTestBuilder aSavingsAccount(User owner) {
        return new BankingAccountTestBuilder()
            .savings()
            .active()
            .withAlias("Savings account")
            .withCurrency(BankingAccountCurrency.EUR)
            .withOwner(owner);
    }

    public static BankingAccountTestBuilder aCheckingAccount(User owner) {
        return new BankingAccountTestBuilder()
            .withOwner(owner)
            .active()
            .withAlias("Checking account")
            .withCurrency(BankingAccountCurrency.EUR)
            .checking();
    }
}