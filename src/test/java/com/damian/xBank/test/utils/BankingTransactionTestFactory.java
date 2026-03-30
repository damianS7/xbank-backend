package com.damian.xBank.test.utils;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionPaymentStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BankingTransactionTestFactory {

    public static BankingTransactionTestBuilder aDepositTransaction() {
        return BankingTransactionTestBuilder.builder()
            .withType(BankingTransactionType.DEPOSIT)
            .withPaymentStatus(BankingTransactionPaymentStatus.PENDING)
            .withStatus(BankingTransactionStatus.PENDING)
            .withDescription("Deposit transaction");
    }

    public static BankingTransactionTestBuilder aCardChargeTransaction() {
        return BankingTransactionTestBuilder.builder()
            .withType(BankingTransactionType.CARD_CHARGE)
            .withPaymentStatus(BankingTransactionPaymentStatus.PENDING)
            .withStatus(BankingTransactionStatus.PENDING)
            .withDescription("Deposit transaction");
    }
}