package com.damian.xBank.test.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;

public class BankingCardTestFactory {
    public static BankingCardTestBuilder aDebitCard(BankingAccount bankingAccount) {
        return new BankingCardTestBuilder()
            .withOwnerAccount(bankingAccount);
    }
}