package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferDomainService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.service.NotificationDomainService;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountOperationService {
    private final BankingAccountRepository bankingAccountRepository;
    private final NotificationDomainService notificationDomainService;
    private final BankingTransferDomainService bankingTransferDomainService;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingTransferRepository bankingTransferRepository;

    public BankingAccountOperationService(
            BankingAccountRepository bankingAccountRepository,
            NotificationDomainService notificationDomainService,
            BankingTransferDomainService bankingTransferDomainService,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator,
            BankingTransferRepository bankingTransferRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.notificationDomainService = notificationDomainService;
        this.bankingTransferDomainService = bankingTransferDomainService;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingTransferRepository = bankingTransferRepository;
    }


}