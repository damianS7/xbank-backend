package com.damian.xBank.modules.banking.transaction.domain.model;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import org.springframework.stereotype.Component;

@Component
public class BankingTransactionFactory {
    public BankingTransaction createTransferFrom(BankingTransfer transfer) {
        BankingTransaction transaction = new BankingTransaction();
        transaction.setBankingAccount(transfer.getFromAccount());
        transaction.setTransfer(transfer);
        transaction.setType(BankingTransactionType.TRANSFER_FROM);
        transaction.setAmount(transfer.getAmount());
        transaction.setDescription(transfer.getDescription());
        transaction.setBalanceBefore(transfer.getFromAccount().getBalance());
        transaction.setBalanceAfter(transfer.getFromAccount().getBalance().add(transfer.getAmount()));
        return transaction;
    }
}
