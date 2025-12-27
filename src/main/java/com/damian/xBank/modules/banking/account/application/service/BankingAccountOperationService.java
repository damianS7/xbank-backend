package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountOperationService {
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationService notificationService;
    private final BankingTransferService bankingTransferService;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingTransferRepository bankingTransferRepository;

    public BankingAccountOperationService(
            BankingAccountRepository bankingAccountRepository,
            NotificationService notificationService,
            BankingTransferService bankingTransferService,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator,
            BankingTransferRepository bankingTransferRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationService = notificationService;
        this.bankingTransferService = bankingTransferService;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingTransferRepository = bankingTransferRepository;
    }


}