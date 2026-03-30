package com.damian.xBank.test.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionPaymentStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;

import java.math.BigDecimal;
import java.time.Instant;

public class BankingTransactionTestBuilder {
    private Long transactionId;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;
    private OutgoingTransfer outgoingTransfer;
    private IncomingTransfer incomingTransfer;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal balanceBefore = BigDecimal.ZERO;
    private BigDecimal balanceAfter = BigDecimal.ZERO;
    private String description = "";
    private String authorizationId = "";
    private BankingTransactionType type = BankingTransactionType.DEPOSIT;
    private BankingTransactionStatus status = BankingTransactionStatus.PENDING;
    private BankingTransactionPaymentStatus paymentStatus = BankingTransactionPaymentStatus.PENDING;

    public static BankingTransactionTestBuilder builder() {
        return new BankingTransactionTestBuilder();
    }

    public BankingTransactionTestBuilder rejected() {
        this.status = BankingTransactionStatus.REJECTED;
        return this;
    }

    public BankingTransactionTestBuilder completed() {
        this.status = BankingTransactionStatus.COMPLETED;
        return this;
    }

    public BankingTransactionTestBuilder pending() {
        this.status = BankingTransactionStatus.PENDING;
        return this;
    }

    public BankingTransactionTestBuilder authorized() {
        this.paymentStatus = BankingTransactionPaymentStatus.AUTHORIZED;
        return this;
    }

    public BankingTransactionTestBuilder captured() {
        this.paymentStatus = BankingTransactionPaymentStatus.CAPTURED;
        return this;
    }

    public BankingTransactionTestBuilder failed() {
        this.paymentStatus = BankingTransactionPaymentStatus.FAILED;
        return this;
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

    public BankingTransactionTestBuilder withPaymentStatus(BankingTransactionPaymentStatus status) {
        this.paymentStatus = status;
        return this;
    }

    public BankingTransactionTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public BankingTransactionTestBuilder withAuthorizationId(String authorizarionId) {
        this.authorizationId = authorizarionId;
        return this;
    }

    public BankingTransactionTestBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BankingTransactionTestBuilder withBalanceBefore(BigDecimal amount) {
        this.balanceBefore = amount;
        return this;
    }

    public BankingTransactionTestBuilder withBalanceAfter(BigDecimal amount) {
        this.balanceAfter = amount;
        return this;
    }

    public BankingTransaction build() {
        return BankingTransaction.reconstitute(
            transactionId,
            bankingAccount,
            bankingCard,
            outgoingTransfer,
            incomingTransfer,
            amount,
            balanceBefore,
            balanceAfter,
            description,
            authorizationId,
            type,
            status,
            paymentStatus,
            Instant.now(),
            Instant.now()
        );
    }
}