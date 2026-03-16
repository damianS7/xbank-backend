package com.damian.xBank.modules.banking.transaction.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;

import java.math.BigDecimal;

public class BankingTransactionTestBuilder {
    private Long transactionId;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;
    private OutgoingTransfer outgoingTransfer;
    private IncomingTransfer incomingTransfer;
    private BigDecimal amount;
    private String description;
    private BankingTransactionType type;
    private BankingTransactionStatus status;

    public static BankingTransactionTestBuilder builder() {
        return new BankingTransactionTestBuilder();
    }

    public BankingTransactionTestBuilder withId(Long id) {
        this.transactionId = id;
        return this;
    }

    public BankingTransactionTestBuilder withAccount(BankingAccount account) {
        this.bankingAccount = account;
        return this;
    }

    public BankingTransactionTestBuilder withCard(BankingCard card) {
        this.bankingCard = card;
        this.bankingAccount = card.getBankingAccount();
        return this;
    }

    public BankingTransactionTestBuilder withTransfer(OutgoingTransfer transfer) {
        this.outgoingTransfer = transfer;
        return this;
    }

    public BankingTransactionTestBuilder withType(BankingTransactionType type) {
        this.type = type;
        return this;
    }

    public BankingTransactionTestBuilder withStatus(BankingTransactionStatus status) {
        this.status = status;
        return this;
    }

    public BankingTransactionTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public BankingTransactionTestBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BankingTransaction build() {
        return new BankingTransaction(
            transactionId,
            bankingAccount,
            bankingCard,
            outgoingTransfer,
            incomingTransfer,
            amount,
            description,
            type,
            status
        );
    }
}