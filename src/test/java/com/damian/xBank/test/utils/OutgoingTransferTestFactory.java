package com.damian.xBank.test.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;

public class OutgoingTransferTestFactory {
    public static OutgoingTransferTestBuilder anInternalTransfer(BankingAccount from, BankingAccount to) {
        return new OutgoingTransferTestBuilder()
            .withFromAccount(from)
            .withToAccount(to)
            .internal();
    }

    public static OutgoingTransferTestBuilder aExternalTransfer(BankingAccount from, String toIban) {
        return new OutgoingTransferTestBuilder()
            .withFromAccount(from)
            .withToAccountIban(toIban)
            .external();
    }
}