package com.damian.xBank.modules.banking.account.application.service.admin;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountDepositRequest;
import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.application.guard.BankingAccountGuard;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.modules.notification.domain.enums.NotificationType;
import com.damian.xBank.modules.notification.domain.event.NotificationEvent;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class AdminBankingAccountOperationService {
    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationService notificationService;

    public AdminBankingAccountOperationService(
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
        BankingAccountGuard.forAccount(bankingAccount)
                           .active();

        BankingTransaction transaction = bankingTransactionAccountService.generateTransaction(
                bankingAccount,
                BankingTransactionType.DEPOSIT,
                request.amount(),
                "DEPOSIT by " + request.depositorName()
        );

        // if the transaction is created, add the amount to balance
        bankingAccount.addBalance(request.amount());

        // transaction is completed
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // save the transaction
        return bankingTransactionAccountService.persistTransaction(transaction);
    }

    public BankingTransaction transfer(
            Long fromBankingAccountId,
            BankingAccountTransferRequest request
    ) {

        // Banking account to receive funds
        final BankingAccount fromBankingAccount = bankingAccountRepository
                .findById(fromBankingAccountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                Exceptions.BANKING.ACCOUNT.NOT_FOUND, fromBankingAccountId
                        )
                );

        return this.transferTo(
                fromBankingAccount,
                request.toBankingAccountNumber(),
                request.password(),
                request.amount(),
                request.description()
        );
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
        BankingAccountGuard
                .forAccount(fromBankingAccount)
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

        fromBankingAccount.subtractBalance(amount);
        fromTransaction.setStatus(BankingTransactionStatus.COMPLETED);
        this.bankingTransactionAccountService.persistTransaction(fromTransaction);

        // create transfer transaction for the receiver of the funds
        BankingTransaction toTransaction = this.bankingTransactionAccountService.createTransaction(
                toBankingAccount,
                BankingTransactionType.TRANSFER_FROM,
                amount,
                "Transfer from " + fromBankingAccount.getOwner().getFullName()
        );

        toBankingAccount.addBalance(amount);
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