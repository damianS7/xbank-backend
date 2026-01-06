package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.user.user.domain.model.User;

import java.math.BigDecimal;

public class BankingAccountTestBuilder {

    private Long id = 1L;
    private User owner;
    private BigDecimal balance = BigDecimal.valueOf(1000);
    private BankingAccountCurrency currency = BankingAccountCurrency.EUR;
    private BankingAccountType accountType = BankingAccountType.SAVINGS;
    private String accountNumber = "ES9900001111112233334444";
    private BankingAccountStatus status = BankingAccountStatus.ACTIVE;

    public static BankingAccountTestBuilder aDefaultAccount() {
        return new BankingAccountTestBuilder();
    }

    public static BankingAccountTestBuilder anAccountWithBalance(BigDecimal balance) {
        return new BankingAccountTestBuilder().withBalance(balance);
    }

    public BankingAccountTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BankingAccountTestBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public BankingAccountTestBuilder withBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public BankingAccountTestBuilder withCurrency(BankingAccountCurrency currency) {
        this.currency = currency;
        return this;
    }

    public BankingAccountTestBuilder withAccountType(BankingAccountType type) {
        this.accountType = type;
        return this;
    }

    public BankingAccountTestBuilder withAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public BankingAccountTestBuilder withStatus(BankingAccountStatus status) {
        this.status = status;
        return this;
    }

    public BankingAccount build() {
        return BankingAccount.create(owner)
                             .setId(id)
                             .setBalance(balance)
                             .setCurrency(currency)
                             .setType(accountType)
                             .setAccountNumber(accountNumber)
                             .setStatus(status);
    }
}