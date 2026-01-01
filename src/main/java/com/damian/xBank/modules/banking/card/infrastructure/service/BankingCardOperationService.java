package com.damian.xBank.modules.banking.card.infrastructure.service;

import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

@Service
public class BankingCardOperationService {


    private final BankingCardRepository bankingCardRepository;
    private final NotificationPublisher notificationPublisher;
    private final AuthenticationContext authenticationContext;

    public BankingCardOperationService(
            BankingCardRepository bankingCardRepository,
            NotificationPublisher notificationPublisher,
            AuthenticationContext authenticationContext
    ) {
        this.bankingCardRepository = bankingCardRepository;
        this.notificationPublisher = notificationPublisher;
        this.authenticationContext = authenticationContext;
    }

}
