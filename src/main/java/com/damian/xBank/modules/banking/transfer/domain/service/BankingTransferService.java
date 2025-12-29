package com.damian.xBank.modules.banking.transfer.domain.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingTransferService {

    private final BankingTransactionService bankingTransactionService;

    public BankingTransferService(
            BankingTransactionService bankingTransactionService
    ) {
        this.bankingTransactionService = bankingTransactionService;
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
            Long customerId,
            BankingAccount fromAccount,
            BankingAccount toAccount,
            BigDecimal amount,
            String description
    ) {
        // assert customerId is the owner of fromAccount
        fromAccount.assertOwnedBy(customerId);

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
                .setDescription("Transfer from " + fromAccount.getOwner().getFullName());

        transfer.addTransaction(toTransaction);

        return transfer;
    }

    /**
     * Confirms a pending transfer.
     *
     * @param customerId
     * @param transfer
     * @return the confirmed transfer
     */
    public BankingTransfer confirmTransfer(Long customerId, BankingTransfer transfer) {
        // assert that the transfer belongs to customerId
        transfer.assertOwnedBy(customerId);

        // deduct balance
        BankingAccount fromAccount = transfer.getFromAccount();
        fromAccount.subtractBalance(transfer.getAmount());

        // add balance
        BankingAccount toAccount = transfer.getToAccount();
        toAccount.addBalance(transfer.getAmount());

        // confirm transfer
        transfer.confirm();

        // Confirm transactions
        transfer.getTransactions().forEach(bankingTransactionService::complete);

        return transfer;
    }

    /**
     * Rejects a pending transfer
     *
     * @param customerId
     * @param transfer
     * @return the rejected transfer
     */
    public BankingTransfer reject(Long customerId, BankingTransfer transfer) {
        // assert that the transfer belongs to customerId
        transfer.assertOwnedBy(customerId);

        // reject transfer
        transfer.reject();

        // reject transactions
        transfer.getTransactions().forEach(bankingTransactionService::reject);

        return transfer;
    }

}
