package com.damian.xBank.modules.banking.transfer.outgoing.application.scheduler;

import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize.AuthorizeOutgoingExternalTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize.AuthorizeOutgoingInternalTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize.AuthorizeOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutgoingTransferAuthorizationJob {

    private static final Logger log = LoggerFactory.getLogger(OutgoingTransferAuthorizationJob.class);
    private final OutgoingTransferRepository outgoingTransferRepository;
    private final AuthorizeOutgoingInternalTransfer authorizeOutgoingInternalTransfer;
    private final AuthorizeOutgoingExternalTransfer authorizeOutgoingExternalTransfer;

    public OutgoingTransferAuthorizationJob(
        OutgoingTransferRepository outgoingTransferRepository,
        AuthorizeOutgoingInternalTransfer authorizeOutgoingInternalTransfer,
        AuthorizeOutgoingExternalTransfer authorizeOutgoingExternalTransfer
    ) {
        this.outgoingTransferRepository = outgoingTransferRepository;
        this.authorizeOutgoingInternalTransfer = authorizeOutgoingInternalTransfer;
        this.authorizeOutgoingExternalTransfer = authorizeOutgoingExternalTransfer;
    }

    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void authorizeTransfers() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<OutgoingTransfer> page;

        do {
            page = outgoingTransferRepository.findAllByStatus(
                OutgoingTransferStatus.CONFIRMED,
                pageable
            );

            for (OutgoingTransfer transfer : page.getContent()) {
                log.debug("Authorizing transfer: {}", transfer.toString());
                AuthorizeOutgoingTransferCommand command = new AuthorizeOutgoingTransferCommand(transfer.getId());
                if (transfer.getType() == OutgoingTransferType.INTERNAL) {
                    authorizeOutgoingInternalTransfer.execute(command);
                }

                if (transfer.getType() == OutgoingTransferType.EXTERNAL) {
                    authorizeOutgoingExternalTransfer.execute(command);
                }
            }

            pageable = pageable.next();

        } while (page.hasNext());
    }
}
