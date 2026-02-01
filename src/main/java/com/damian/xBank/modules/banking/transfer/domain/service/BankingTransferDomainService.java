package com.damian.xBank.modules.banking.transfer.domain.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingTransferDomainService {

    public BankingTransferDomainService() {
    }

    /**
     * Create a new transfer and stores it as PENDING.
     * It does not commit any funds until the transfer is confirmed.
     *
     * @param fromAccount
     * @param toAccount
     * @param amount
     * @param description
     * @return the created BankingTransfer
     */
    public BankingTransfer createTransfer(
            Long userId,
            BankingAccount fromAccount,
            BankingAccount toAccount,
            BigDecimal amount,
            String description
    ) {
        // assert userId is the owner of fromAccount
        fromAccount.assertOwnedBy(userId);

        // Create the transfer
        BankingTransfer transfer = BankingTransfer
                .create(fromAccount, toAccount, amount)
                .setDescription(description);

        // validate transfer
        transfer.assertTransferPossible();

        // Generate transactions
        BankingTransaction fromTransaction = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_TO,
                        fromAccount,
                        amount
                )
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription(description);

        transfer.addTransaction(fromTransaction);

        // create transfer transaction for the receiver of the funds
        BankingTransaction toTransaction = BankingTransaction
                .create(
                        BankingTransactionType.TRANSFER_FROM,
                        toAccount,
                        amount
                )
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription("Transfer from " + fromAccount.getOwner().getProfile().getFullName());

        transfer.addTransaction(toTransaction);

        return transfer;
    }

    /**
     * Confirms a pending transfer.
     *
     * @param userId
     * @param transfer
     * @return the confirmed transfer
     */
    public BankingTransfer confirmTransfer(Long userId, BankingTransfer transfer) {
        // assert that the transfer belongs to userId
        transfer.assertOwnedBy(userId);

        // Confirm transactions
        transfer.getTransactions().forEach(BankingTransaction::complete);

        // deduct balance
        BankingAccount fromAccount = transfer.getFromAccount();
        fromAccount.subtractBalance(transfer.getAmount());

        // add balance
        BankingAccount toAccount = transfer.getToAccount();
        toAccount.addBalance(transfer.getAmount());

        // confirm transfer
        transfer.confirm();

        return transfer;
    }

    /**
     * Rejects a pending transfer
     *
     * @param userId
     * @param transfer
     * @return the rejected transfer
     */
    public BankingTransfer reject(Long userId, BankingTransfer transfer) {
        // assert that the transfer belongs to userId
        transfer.assertOwnedBy(userId);

        // reject transfer
        transfer.reject();

        // reject transactions
        transfer.getTransactions().forEach(BankingTransaction::decline);

        return transfer;
    }

}
