package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import com.damian.xBank.shared.AbstractServiceTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class BankingAccountOperationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private BankingAccountOperationService bankingAccountOperationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;


}