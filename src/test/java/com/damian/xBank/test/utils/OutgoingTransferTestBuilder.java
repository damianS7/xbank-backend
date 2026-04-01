package com.damian.xBank.test.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OutgoingTransferTestBuilder {
    private Long id = null;
    private BankingAccount fromAccount;
    private BankingAccount toAccount;
    private String toAccountIban = "ES9900001111112233334444";
    private BigDecimal amount = BigDecimal.valueOf(0);
    private String description = "Test transfer";
    private OutgoingTransferType type = OutgoingTransferType.INTERNAL;
    private OutgoingTransferStatus status = OutgoingTransferStatus.PENDING;
    private String providerAuthorizationId = "1234/1234";
    private List<BankingTransaction> transactions = new ArrayList<>();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public static OutgoingTransferTestBuilder builder() {
        return new OutgoingTransferTestBuilder();
    }

    public OutgoingTransferTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OutgoingTransferTestBuilder internal() {
        this.type = OutgoingTransferType.INTERNAL;
        return this;
    }

    public OutgoingTransferTestBuilder external() {
        this.type = OutgoingTransferType.EXTERNAL;
        return this;
    }

    public OutgoingTransferTestBuilder pending() {
        this.status = OutgoingTransferStatus.PENDING;
        return this;
    }

    public OutgoingTransferTestBuilder confirmed() {
        this.status = OutgoingTransferStatus.CONFIRMED;
        return this;
    }

    public OutgoingTransferTestBuilder authorized() {
        this.status = OutgoingTransferStatus.AUTHORIZED;
        return this;
    }

    public OutgoingTransferTestBuilder withFromAccount(BankingAccount fromAccount) {
        this.fromAccount = fromAccount;
        return this;
    }

    public OutgoingTransferTestBuilder withToAccount(BankingAccount toAccount) {
        this.toAccount = toAccount;
        this.toAccountIban = toAccount.getAccountNumber();
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

        OutgoingTransfer transfer = OutgoingTransfer.reconstitute(
            id,
            fromAccount,
            toAccount,
            toAccountIban,
            amount,
            type,
            status,
            providerAuthorizationId,
            description,
            transactions,
            createdAt,
            updatedAt
        );

        // Transacción de salida
        BankingTransaction fromTransaction = BankingTransaction.createOutgoingTransferTransaction(
            BankingTransactionType.OUTGOING_TRANSFER,
            fromAccount,
            transfer,
            description
        );

        this.transactions.add(fromTransaction);

        // Si el destino existe, crear transacción para el receptor
        if (toAccount != null) {
            BankingTransaction toTransaction = BankingTransaction.createOutgoingTransferTransaction(
                BankingTransactionType.INCOMING_TRANSFER,
                toAccount,
                transfer,
                "Transfer from " + fromAccount.getOwner().getProfile().getFullName()
            );

            this.transactions.add(toTransaction);
        }

        return transfer;
    }
}