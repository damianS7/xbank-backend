package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.complete;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferType;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteOutgoingInternalTransfer {
    private final BankingTransferRepository bankingTransferRepository;
    private final NotificationPublisher notificationPublisher;
    private final NotificationEventFactory notificationEventFactory;

    public CompleteOutgoingInternalTransfer(
        BankingTransferRepository bankingTransferRepository,
        NotificationPublisher notificationPublisher,
        NotificationEventFactory notificationEventFactory
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
        this.notificationPublisher = notificationPublisher;
        this.notificationEventFactory = notificationEventFactory;
    }

    @Transactional
    public void execute(BankingTransfer transfer) {
        transfer.complete();

        // TODO check this notifications ...
        // Notify sender
        notificationPublisher.publish(
            notificationEventFactory.transferAuthorized(transfer)
        );

        if (transfer.getType() == BankingTransferType.INTERNAL) {
            // Notify recipient
            notificationPublisher.publish(
                notificationEventFactory.transferReceived(transfer)
            );
        }

        bankingTransferRepository.save(transfer);
    }
}