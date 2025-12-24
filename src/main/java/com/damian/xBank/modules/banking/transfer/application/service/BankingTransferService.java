package com.damian.xBank.modules.banking.transfer.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRequest;
import com.damian.xBank.modules.banking.transfer.domain.entity.BankingTransfer;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

@Service
public class BankingTransferService {

    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationService notificationService;
    private final PasswordValidator passwordValidator;
    private final AuthenticationContext authenticationContext;

    public BankingTransferService(
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
     * Initiate a transfer from one banking account to another.
     * It generates the transaction but does not commit any funds yet.
     *
     * @param request Transfer request containing the details
     * @return the created BankingTransfer
     */
    public BankingTransfer initiate(BankingTransferRequest request) {

        // Customer logged
        final Customer customer = authenticationContext.getCurrentCustomer();

        // Banking account from where funds will be transfered.
        final BankingAccount fromAccount = bankingAccountRepository
                .findById(request.fromAccountId())
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(request.fromAccountId())
                );

        return null;
    }

    public void confirm(Long transferId) {

    }

    public void reject(Long transferId) {

    }

}
