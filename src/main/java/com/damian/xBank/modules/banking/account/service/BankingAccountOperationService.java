package com.damian.xBank.modules.banking.account.service;

import com.damian.xBank.modules.banking.account.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.account.validator.BankingAccountValidator;
import com.damian.xBank.modules.banking.transactions.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transactions.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transactions.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transactions.service.BankingTransactionAccountService;
import com.damian.xBank.modules.notification.enums.NotificationType;
import com.damian.xBank.modules.notification.service.NotificationService;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.BankingTransaction;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.notification.event.NotificationEvent;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class BankingAccountOperationService {
    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationService notificationService;

    public BankingAccountOperationService(
            BankingTransactionAccountService bankingTransactionAccountService,
            BankingAccountRepository bankingAccountRepository,
            NotificationService notificationService
    ) {
        this.bankingTransactionAccountService = bankingTransactionAccountService;
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationService = notificationService;
    }

    /**
     * Deposit into banking account
     *
     * @param bankingAccountId
     * @param request
     * @return BankingTransaction
     */
    public BankingTransaction deposit(
            Long bankingAccountId,
            BankingAccountDepositRequest request
    ) {
        if (!request.transactionType().equals(BankingTransactionType.DEPOSIT)) {
            // throw invalid transaction type
        }

        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // TODO check currentCustomer is an employee or admin

        // The account to deposit into
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(bankingAccountId).orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                bankingAccountId
                        ) // Banking account not found
                );

        // Validate account
        BankingAccountValidator.validate(bankingAccount)
                               .active();

        BankingTransaction transaction = bankingTransactionAccountService.generateTransaction(
                bankingAccount,
                BankingTransactionType.DEPOSIT,
                request.amount(),
                "DEPOSIT by " + request.depositorName()
        );

        // if the transaction is created, add the amount to balance
        bankingAccount.deposit(request.amount());

        // transaction is completed
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // save the transaction
        return bankingTransactionAccountService.persistTransaction(transaction);
    }

    public BankingTransaction transfer(
            Long fromBankingAccountId,
            BankingAccountTransferRequest request
    ) {
        return null;
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

        AuthHelper.validatePassword(customer, password);

        // run validations and throw if any throw exception
        BankingAccountValidator
                .validate(fromBankingAccount)
                .ownership(customer)
                .transfer(toBankingAccount, amount);

        return this.transferTo(fromBankingAccount, toBankingAccount, amount, description);
    }

    public BankingTransaction transferTo(
            BankingAccount fromBankingAccount,
            BankingAccount toBankingAccount,
            BigDecimal amount,
            String description
    ) {
        BankingTransaction fromTransaction = this.bankingTransactionAccountService.createTransaction(
                fromBankingAccount,
                BankingTransactionType.TRANSFER_TO,
                amount,
                description
        );

        fromBankingAccount.subtractAmount(amount);
        fromTransaction.setStatus(BankingTransactionStatus.COMPLETED);
        this.bankingTransactionAccountService.persistTransaction(fromTransaction);

        // create transfer transaction for the receiver of the funds
        BankingTransaction toTransaction = this.bankingTransactionAccountService.createTransaction(
                toBankingAccount,
                BankingTransactionType.TRANSFER_FROM,
                amount,
                "Transfer from " + fromBankingAccount.getOwner().getFullName()
        );

        toBankingAccount.deposit(amount);
        toTransaction.setStatus(BankingTransactionStatus.COMPLETED);
        this.bankingTransactionAccountService.persistTransaction(toTransaction);

        // TODO: Notify both parties about the transaction
        notificationService.publishNotification(
                new NotificationEvent(
                        toBankingAccount.getOwner().getId(),
                        NotificationType.TRANSACTION,
                        Map.of(
                                "transaction", BankingTransactionDtoMapper.toBankingTransactionDto(toTransaction)
                        ),
                        Instant.now().toString()
                )
        );

        return fromTransaction;
    }
}