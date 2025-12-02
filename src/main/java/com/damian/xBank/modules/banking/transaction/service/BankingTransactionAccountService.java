package com.damian.xBank.modules.banking.transaction.service;

import com.damian.xBank.modules.banking.account.model.BankingAccount;
import com.damian.xBank.modules.banking.account.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.dto.request.BankingTransactionUpdateStatusRequest;
import com.damian.xBank.modules.banking.transaction.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.exception.BankingTransactionAuthorizationException;
import com.damian.xBank.modules.banking.transaction.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.model.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class BankingTransactionAccountService {
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransactionRepository bankingTransactionRepository;

    public BankingTransactionAccountService(
            BankingAccountRepository bankingAccountRepository,
            BankingTransactionRepository bankingTransactionRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
    }

    public BankingTransaction createTransaction(
            BankingAccount bankingAccount,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        return BankingTransaction.create()
                                 .setAssociatedBankingAccount(bankingAccount)
                                 .setTransactionType(transactionType)
                                 .setLastBalance(bankingAccount.getBalance())
                                 .setAmount(amount)
                                 .setDescription(description);
    }

    public Page<BankingTransaction> getTransactions(Long bankingAccountId, Pageable pageable) {
        return bankingTransactionRepository.findByBankingAccountId(bankingAccountId, pageable);
    }

    // ...

    public BankingTransaction generateTransaction(
            BankingAccount bankingAccount,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        // createTransaction
        BankingTransaction transaction = this.createTransaction(bankingAccount, transactionType, amount, description);

        // persistTransaction
        this.persistTransaction(transaction);

        return transaction;
    }

    /**
     * Stores a banking account transaction by adding it to the owner's account and persisting the account.
     *
     * @param transaction the banking account transaction to store
     * @return the stored banking account transaction
     */
    // TODO rename method to commitTransaction?
    public BankingTransaction persistTransaction(BankingTransaction transaction) {
        final BankingAccount bankingAccount = transaction.getAssociatedBankingAccount();
        transaction.setCreatedAt(Instant.now());
        transaction.setUpdatedAt(Instant.now());

        // Add the transaction to the owners account
        bankingAccount.addAccountTransaction(transaction);

        // Persist the owner's account with the new transaction
        // Return the stored transaction
        return bankingTransactionRepository.save(transaction);
    }

    public BankingTransaction getBankingTransaction(Long transactionId) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        BankingTransaction transaction = bankingTransactionRepository.findById(transactionId).orElseThrow(
                () -> new BankingTransactionNotFoundException(transactionId)
        );

        if (!transaction.getAssociatedBankingAccount().getOwner().getId().equals(currentCustomer.getId())
            && !currentCustomer.getAccount().getRole().equals(UserAccountRole.ADMIN)) {
            throw new BankingTransactionAuthorizationException(
                    Exceptions.BANKING.TRANSACTION.ACCESS_FORBIDDEN, transactionId
            );
        }

        return transaction;
    }


    // it changes the status of the transaction
    public BankingTransaction updateTransactionStatus(
            Long bankingTransactionId,
            BankingTransactionUpdateStatusRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // if the logged customer is not admin
        if (!currentCustomer.getAccount().getRole().equals(UserAccountRole.ADMIN)) {
            // banking transaction does not belong to this customer
            throw new BankingTransactionAuthorizationException(
                    Exceptions.BANKING.TRANSACTION.ACCESS_FORBIDDEN, bankingTransactionId
            );
        }

        // transaction to update
        final BankingTransaction bankingTransaction = bankingTransactionRepository
                .findById(bankingTransactionId)
                .orElseThrow(
                        () -> new BankingTransactionNotFoundException(
                                bankingTransactionId
                        )
                );

        // we mark the account as closed
        bankingTransaction.setStatus(request.transactionStatus());

        // we change the updateAt timestamp field
        bankingTransaction.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingTransactionRepository.save(bankingTransaction);
    }
}