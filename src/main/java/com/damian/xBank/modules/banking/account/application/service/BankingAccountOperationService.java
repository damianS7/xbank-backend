package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountTransferRequest;
import com.damian.xBank.modules.banking.account.application.guard.BankingAccountGuard;
import com.damian.xBank.modules.banking.account.application.guard.BankingAccountOperationGuard;
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
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class BankingAccountOperationService {
    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationService notificationService;
    private final PasswordValidator passwordValidator;
    private final AuthenticationContext authenticationContext;

    public BankingAccountOperationService(
            BankingTransactionAccountService bankingTransactionAccountService,
            BankingAccountRepository bankingAccountRepository,
            NotificationService notificationService,
            PasswordValidator passwordValidator,
            AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionAccountService = bankingTransactionAccountService;
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationService = notificationService;
        this.passwordValidator = passwordValidator;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Transfer funds from one banking account to another.
     *
     * @param fromBankingAccountId ID of the banking account to transfer funds from
     * @param request              Transfer request containing the details
     * @return the created BankingTransaction
     */
    public BankingTransaction transfer(
            Long fromBankingAccountId,
            BankingAccountTransferRequest request
    ) {
        // Banking account to receive funds
        final BankingAccount fromBankingAccount = bankingAccountRepository
                .findById(fromBankingAccountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(fromBankingAccountId)
                );

        final Customer customer = authenticationContext.getCurrentCustomer();

        // run validations and throw if any throw exception
        BankingAccountGuard
                .forAccount(fromBankingAccount)
                .assertOwnership(customer)
                .assertSufficientFunds(request.amount());

        // validate customer password
        passwordValidator.validatePassword(customer, request.password());

        // TODO move this block to the other method?
        // Banking account to receive funds
        final BankingAccount toBankingAccount = bankingAccountRepository
                .findByAccountNumber(request.toBankingAccountNumber())
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(request.toBankingAccountNumber())
                );

        return this.executeTransfer(
                fromBankingAccount,
                toBankingAccount,
                request.amount(),
                request.description()
        );
    }

    /**
     * It generates transactions and perform the transfer between accounts.
     * TODO
     *
     * @param fromBankingAccount
     * @param toBankingAccount
     * @param amount
     * @param description
     * @return
     */ // TODO
    @Transactional
    public BankingTransaction executeTransfer(
            BankingAccount fromBankingAccount,
            BankingAccount toBankingAccount,
            BigDecimal amount,
            String description
    ) {

        // check if fromBankingAccount can transfer to toBankingAccount
        BankingAccountOperationGuard
                .forAccount(fromBankingAccount)
                .assertCanTransfer(toBankingAccount);

        BankingTransaction fromTransaction = this.bankingTransactionAccountService.buildTransaction(
                fromBankingAccount,
                BankingTransactionType.TRANSFER_TO,
                amount,
                description
        );

        // balance after the transfer
        fromTransaction.setBalanceAfter(
                fromBankingAccount.getBalance().subtract(amount)
        );

        fromTransaction.setStatus(BankingTransactionStatus.PENDING);
        this.bankingTransactionAccountService.recordTransaction(fromTransaction);

        // create transfer transaction for the receiver of the funds
        BankingTransaction toTransaction = this.bankingTransactionAccountService.buildTransaction(
                toBankingAccount,
                BankingTransactionType.TRANSFER_FROM,
                amount,
                "Transfer from " + fromBankingAccount.getOwner().getFullName()
        );

        // balance after receiving the transfer
        toTransaction.setBalanceAfter(
                toBankingAccount.getBalance().add(amount)
        );

        toTransaction.setStatus(BankingTransactionStatus.PENDING);
        this.bankingTransactionAccountService.recordTransaction(toTransaction);

        // Notify receiver
        notificationService.publish(
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

    public BankingTransaction confirmTransfer(BankingTransaction transaction) {
        return null;
    }
}