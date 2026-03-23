package com.damian.xBank.modules.banking.transfer.outgoing.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;

import java.math.BigDecimal;

public class OutgoingTransferTestBuilder {
    private Long id = null;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;
    private String toAccountIban;
    private BigDecimal amount = BigDecimal.valueOf(0);
    private String description = "Test transfer";

    public static OutgoingTransferTestBuilder builder() {
        return new OutgoingTransferTestBuilder();
    }

    public OutgoingTransferTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OutgoingTransferTestBuilder withFromAccount(BankingAccount fromAccount) {
        this.fromAccount = fromAccount;
        return this;
    }

    public OutgoingTransferTestBuilder withToAccount(BankingAccount toAccount) {
        this.toAccount = toAccount;
        return this;
    }

    public OutgoingTransferTestBuilder withToAccountIban(String toAccountIban) {
        this.toAccountIban = toAccountIban;
        return this;
    }

    public OutgoingTransferTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public OutgoingTransferTestBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public OutgoingTransfer build() {
        return new OutgoingTransfer(id, fromAccount, toAccount, toAccountIban, amount, description);
    }
}