package com.damian.xBank.modules.banking.transactions.service;

import com.damian.xBank.modules.banking.account.BankingAccountAuthorizationHelper;
import com.damian.xBank.modules.banking.account.exception.BankingAccountAuthorizationException;
import com.damian.xBank.modules.banking.account.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transactions.dto.request.BankingAccountTransactionRequest;
import com.damian.xBank.modules.banking.transactions.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transactions.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transactions.exception.BankingTransactionException;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.BankingTransaction;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingTransactionAccountService {
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransactionService bankingTransactionService;

    public BankingTransactionAccountService(
            BankingAccountRepository bankingAccountRepository,
            BankingTransactionService bankingTransactionService
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingTransactionService = bankingTransactionService;
    }

    // handle request BankingTransactionType and determine what to do.
    public BankingTransaction processTransactionRequest(
            Long fromAccountId,
            BankingAccountTransactionRequest request
    ) {
        // BankingAccount to operate
        BankingAccount bankingAccount = bankingAccountRepository.findById(fromAccountId).orElseThrow(
                () -> new BankingAccountNotFoundException(
                        fromAccountId
                )
        );

        return switch (request.transactionType()) {
            case TRANSFER_TO -> this.transferTo(
                    bankingAccount,
                    request.toBankingAccountNumber(),
                    request.password(),
                    request.amount(),
                    request.description()
            );
            case DEPOSIT -> this.deposit(bankingAccount, request.password(), request.amount());
            default -> throw new BankingTransactionException(
                    Exceptions.BANKING.TRANSACTION.INVALID_TYPE, -1L
            );
        };
    }

    // validates all security checks before transfer
    public void validateTransferOrElseThrow(
            BankingAccount fromBankingAccount,
            BankingAccount toBankingAccount,
            BigDecimal amount
    ) {
        // check bankingAccount and toBankingAccount are not the same
        if (fromBankingAccount.getId().equals(toBankingAccount.getId())) {
            throw new BankingAccountAuthorizationException(
                    Exceptions.BANKING.ACCOUNT.SAME_DESTINATION,
                    toBankingAccount.getId(),
                    fromBankingAccount.getOwner().getId()
            );
        }

        // check currency are the same
        this.checkCurrency(fromBankingAccount, toBankingAccount);

        // check the funds from the sender account
        this.checkFunds(fromBankingAccount, amount);

        // check the account status and see if can be used to operate
        BankingAccountAuthorizationHelper
                .authorize(null, fromBankingAccount)
                .checkAccountStatus();

        BankingAccountAuthorizationHelper
                .authorize(null, toBankingAccount)
                .checkAccountStatus();
    }


    // check the funds from the account
    private void checkFunds(BankingAccount bankingAccount, BigDecimal amount) {
        if (!bankingAccount.hasEnoughFunds(amount)) {
            throw new BankingAccountAuthorizationException(
                    Exceptions.BANKING.ACCOUNT.INSUFFICIENT_FUNDS, bankingAccount.getId(), 0L // TODO
            );
        }
    }

    // check currency are the same
    private void checkCurrency(BankingAccount fromBankingAccount, BankingAccount toBankingAccount) {
        if (!fromBankingAccount.getAccountCurrency()
                               .equals(toBankingAccount.getAccountCurrency())
        ) {
            throw new BankingAccountAuthorizationException(
                    Exceptions.BANKING.TRANSACTION.DIFFERENT_CURRENCY,
                    toBankingAccount.getId(),
                    fromBankingAccount.getOwner().getId()
            );
        }
    }

    // validates account status and does the transaction
    public BankingTransaction transferTo(
            BankingAccount fromBankingAccount,
            String toBankingAccountNumber,
            String password,
            BigDecimal amount,
            String description
    ) {
        // Banking account to receive funds
        final BankingAccount toBankingAccount = bankingAccountRepository
                .findByAccountNumber(toBankingAccountNumber)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                Exceptions.BANKING.ACCOUNT.NOT_FOUND, toBankingAccountNumber
                        )
                );

        final Customer customer = AuthHelper.getCurrentCustomer();

        // check if the account belongs to this customer.
        BankingAccountAuthorizationHelper
                .authorize(customer, fromBankingAccount)
                .checkOwner()
                .checkAccountStatus();

        AuthHelper.validatePassword(customer, password);

        // check transfer is valid
        this.validateTransferOrElseThrow(fromBankingAccount, toBankingAccount, amount);

        return this.transferTo(fromBankingAccount, toBankingAccount, amount, description);
    }

    public BankingTransaction transferTo(
            BankingAccount fromBankingAccount,
            BankingAccount toBankingAccount,
            BigDecimal amount,
            String description
    ) {
        BankingTransaction fromTransaction = this.bankingTransactionService.createTransaction(
                fromBankingAccount,
                BankingTransactionType.TRANSFER_TO,
                amount,
                description
        );

        fromBankingAccount.subtractAmount(amount);
        fromTransaction.setStatus(BankingTransactionStatus.COMPLETED);
        this.bankingTransactionService.persistTransaction(fromTransaction);

        // create transfer transaction for the receiver of the funds
        BankingTransaction toTransaction = this.bankingTransactionService.createTransaction(
                toBankingAccount,
                BankingTransactionType.TRANSFER_FROM,
                amount,
                "Transfer from " + fromBankingAccount.getOwner().getFullName()
        );

        toBankingAccount.deposit(amount);
        toTransaction.setStatus(BankingTransactionStatus.COMPLETED);
        this.bankingTransactionService.persistTransaction(toTransaction);

        return fromTransaction;
    }

    // validates account status and does the transaction
    public BankingTransaction deposit(
            BankingAccount account,
            String password,
            BigDecimal amount
    ) {
        BankingTransaction transaction = this.bankingTransactionService.createTransaction(
                account,
                BankingTransactionType.DEPOSIT,
                amount,
                "DEPOSIT"
        );

        final Customer customer = AuthHelper.getCurrentCustomer();

        // check if the account belongs to this customer.
        BankingAccountAuthorizationHelper
                .authorize(customer, account)
                .checkOwner()
                .checkAccountStatus();

        // if the transaction is created, deduce the amount from balance
        account.deposit(amount);

        // transaction is completed
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // save the transaction
        return bankingTransactionService.persistTransaction(transaction);
    }
}