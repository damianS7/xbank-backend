package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.complete;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteOutgoingInternalTransfer {
    private final OutgoingTransferRepository outgoingTransferRepository;
    private final NotificationPublisher notificationPublisher;
    private final NotificationEventFactory notificationEventFactory;

    public CompleteOutgoingInternalTransfer(
        OutgoingTransferRepository outgoingTransferRepository,
        NotificationPublisher notificationPublisher,
        NotificationEventFactory notificationEventFactory
    ) {
        this.outgoingTransferRepository = outgoingTransferRepository;
        this.notificationPublisher = notificationPublisher;
        this.notificationEventFactory = notificationEventFactory;
    }

    @Transactional
    public void execute(OutgoingTransfer transfer) {
        transfer.complete();

        // TODO check this notifications ...
        // Notify sender
        notificationPublisher.publish(
            notificationEventFactory.transferAuthorized(transfer)
        );

        if (transfer.getType() == OutgoingTransferType.INTERNAL) {
            // Notify recipient
            notificationPublisher.publish(
                notificationEventFactory.transferReceived(transfer)
            );
        }

        outgoingTransferRepository.save(transfer);
    }
}