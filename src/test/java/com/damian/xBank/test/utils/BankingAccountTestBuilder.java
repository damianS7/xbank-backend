package com.damian.xBank.test.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.user.user.domain.model.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class BankingAccountTestBuilder {
    private Long id = null;
    private User owner;
    private BigDecimal balance = BigDecimal.valueOf(0);
    private BigDecimal reservedBalance = BigDecimal.valueOf(0);
    private BankingAccountCurrency currency = BankingAccountCurrency.EUR;
    private BankingAccountType accountType = BankingAccountType.SAVINGS;
    private String alias = "";
    private String accountNumber = "ES9900001111112233334444";
    private BankingAccountStatus status = BankingAccountStatus.ACTIVE;
    private Set<BankingCard> bankingCards = new HashSet<>();

    public static BankingAccountTestBuilder builder() {
        return new BankingAccountTestBuilder();
    }

    public BankingAccountTestBuilder savings() {
        this.accountType = BankingAccountType.SAVINGS;
        return this;
    }

    public BankingAccountTestBuilder checking() {
        this.accountType = BankingAccountType.CHECKING;
        return this;
    }

    public BankingAccountTestBuilder active() {
        this.status = BankingAccountStatus.ACTIVE;
        return this;
    }

    public BankingAccountTestBuilder suspended() {
        this.status = BankingAccountStatus.SUSPENDED;
        return this;
    }

    public BankingAccountTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BankingAccountTestBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public BankingAccountTestBuilder withCards(Set<BankingCard> bankingCards) {
        this.bankingCards = new HashSet<>(bankingCards);
        return this;
    }

    public BankingAccountTestBuilder withBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public BankingAccountTestBuilder withReservedBalance(BigDecimal balance) {
        this.reservedBalance = balance;
        return this;
    }

    public BankingAccountTestBuilder withCurrency(BankingAccountCurrency currency) {
        this.currency = currency;
        return this;
    }

    public BankingAccountTestBuilder withType(BankingAccountType type) {
        this.accountType = type;
        return this;
    }

    public BankingAccountTestBuilder withAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public BankingAccountTestBuilder withAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public BankingAccountTestBuilder withStatus(BankingAccountStatus status) {
        this.status = status;
        return this;
    }

    public BankingAccount build() {
        return BankingAccount.reconstitute(
            id,
            owner,
            accountNumber,
            alias,
            balance,
            reservedBalance,
            accountType,
            currency,
            status,
            Instant.now(),
            Instant.now(),
            new HashSet<>(this.bankingCards)
        );
    }
}