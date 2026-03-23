package com.damian.xBank.modules.banking.transfer.outgoing.application.scheduler;

import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.complete.CompleteOutgoingInternalTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
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
public class OutgoingTransferCompletionJob {

    private static final Logger log = LoggerFactory.getLogger(OutgoingTransferCompletionJob.class);
    private final OutgoingTransferRepository outgoingTransferRepository;
    private final CompleteOutgoingInternalTransfer completeOutgoingInternalTransfer;

    public OutgoingTransferCompletionJob(
        OutgoingTransferRepository outgoingTransferRepository,
        CompleteOutgoingInternalTransfer completeOutgoingInternalTransfer
    ) {
        this.outgoingTransferRepository = outgoingTransferRepository;
        this.completeOutgoingInternalTransfer = completeOutgoingInternalTransfer;
    }

    /**
     *
     */
    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void completeTransfers() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<OutgoingTransfer> page;

        do {
            page = outgoingTransferRepository.findAllByStatus(
                OutgoingTransferStatus.AUTHORIZED,
                pageable
            );

            for (OutgoingTransfer transfer : page.getContent()) {
                log.debug("Completing transfer: {}", transfer.toString());
                completeOutgoingInternalTransfer.execute(transfer);
            }

            pageable = pageable.next();

        } while (page.hasNext());
    }
}
