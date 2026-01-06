package com.damian.xBank.modules.banking.account.domain.factory;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class BankingAccountFactory {
    public static BankingAccount createFor(User user) {
        return BankingAccount.create(user)
                             .setAccountNumber("US9900001111222233334444");
    }
}