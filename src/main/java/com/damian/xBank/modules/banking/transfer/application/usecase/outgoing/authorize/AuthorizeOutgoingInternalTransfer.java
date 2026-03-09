package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizeOutgoingInternalTransfer {
    private final BankingTransferRepository bankingTransferRepository;
    private final NotificationPublisher notificationPublisher;
    private final NotificationEventFactory notificationEventFactory;

    public AuthorizeOutgoingInternalTransfer(
        BankingTransferRepository bankingTransferRepository,
        NotificationPublisher notificationPublisher,
        NotificationEventFactory notificationEventFactory
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
        this.notificationPublisher = notificationPublisher;
        this.notificationEventFactory = notificationEventFactory;
    }

    @Transactional
    public void execute(AuthorizeOutgoingTransferCommand command) {
        BankingTransfer transfer = bankingTransferRepository
            .findById(command.transferId())
            .orElseThrow();

        transfer.authorize();

        // Notify recipient
        notificationPublisher.publish(
            notificationEventFactory.transferReceived(transfer)
        );

        // Notify sender
        notificationPublisher.publish(
            notificationEventFactory.transferAuthorized(transfer)
        );

        bankingTransferRepository.save(transfer);
    }
}