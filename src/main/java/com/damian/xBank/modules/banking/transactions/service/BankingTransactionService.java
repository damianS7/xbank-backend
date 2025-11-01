package com.damian.xBank.modules.banking.transactions.service;

import com.damian.xBank.modules.banking.transactions.dto.request.BankingTransactionUpdateStatusRequest;
import com.damian.xBank.modules.banking.transactions.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transactions.exception.BankingTransactionAuthorizationException;
import com.damian.xBank.modules.banking.transactions.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transactions.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.BankingCard;
import com.damian.xBank.shared.domain.BankingTransaction;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class BankingTransactionService {
    private final BankingTransactionRepository bankingTransactionRepository;

    public BankingTransactionService(
            BankingTransactionRepository bankingTransactionRepository
    ) {
        this.bankingTransactionRepository = bankingTransactionRepository;
    }

    public Page<BankingTransaction> getBankingCardTransactions(Long bankingCardId, Pageable pageable) {
        return bankingTransactionRepository.findByBankingCardId(bankingCardId, pageable);
    }

    public Page<BankingTransaction> getBankingAccountTransactions(Long accountId, Pageable pageable) {
        return bankingTransactionRepository.findByBankingAccountId(accountId, pageable);
    }

    public BankingTransaction createTransaction(
            BankingCard fromBankingCard,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        return this.createTransaction(
                fromBankingCard.getAssociatedBankingAccount(),
                transactionType,
                amount,
                description
        );
    }

    public BankingTransaction createTransaction(
            BankingAccount bankingAccount,
            BankingTransactionType transactionType,
            BigDecimal amount,
            String description
    ) {
        BankingTransaction transaction = new BankingTransaction(bankingAccount);
        transaction.setTransactionType(transactionType);
        transaction.setLastBalance(bankingAccount.getBalance());
        transaction.setAmount(amount);
        transaction.setDescription(description);
        return transaction;
    }

    /**
     * Stores a banking account transaction by adding it to the owner's account and persisting the account.
     *
     * @param transaction the banking account transaction to store
     * @return the stored banking account transaction
     */
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
