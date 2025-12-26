package com.damian.xBank.modules.banking.transfer.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.domain.entity.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BankingTransferService {

    private final BankingTransactionService bankingTransactionService;
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransferRepository transferRepository;
    private final AuthenticationContext authenticationContext;

    public BankingTransferService(
            BankingTransactionService bankingTransactionService,
            BankingAccountRepository bankingAccountRepository,
            BankingTransferRepository transferRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionService = bankingTransactionService;
        this.bankingAccountRepository = bankingAccountRepository;
        this.transferRepository = transferRepository;
        this.authenticationContext = authenticationContext;
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
    @Transactional
    public BankingTransfer createTransfer(
            BankingAccount fromAccount,
            BankingAccount toAccount,
            BigDecimal amount,
            String description
    ) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // assert currentCustomer is the owner of fromAccount
        fromAccount.assertOwnedBy(currentCustomer.getId());

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

        return transferRepository.save(transfer);
    }

    /**
     * Confirms a pending transfer.
     *
     * @param transfer
     * @return
     */
    @Transactional
    public BankingTransfer confirmTransfer(BankingTransfer transfer) {

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

        // Save
        bankingAccountRepository.save(fromAccount);
        bankingAccountRepository.save(toAccount);

        return transferRepository.save(transfer);
    }

    public BankingTransfer reject(Long transferId) {
        BankingTransfer transfer = transferRepository.findById(transferId).orElseThrow(
                () -> new BankingTransferNotFoundException(transferId)
        );

        transfer.reject();

        return transferRepository.save(transfer);
    }

}
