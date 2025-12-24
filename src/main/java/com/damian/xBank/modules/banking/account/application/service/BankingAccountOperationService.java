package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

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

}