package com.damian.xBank.modules.banking.transfer.application.scheduler;

import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.complete.CompleteOutgoingInternalTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
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
    private final BankingTransferRepository bankingTransferRepository;
    private final CompleteOutgoingInternalTransfer completeOutgoingInternalTransfer;

    public OutgoingTransferCompletionJob(
        BankingTransferRepository bankingTransferRepository,
        CompleteOutgoingInternalTransfer completeOutgoingInternalTransfer
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
        this.completeOutgoingInternalTransfer = completeOutgoingInternalTransfer;
    }

    /**
     *
     */
    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void completeTransfers() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<BankingTransfer> page;

        do {
            page = bankingTransferRepository.findAllByStatus(
                BankingTransferStatus.AUTHORIZED,
                pageable
            );

            for (BankingTransfer transfer : page.getContent()) {
                log.debug("Completing transfer: {}", transfer.toString());
                completeOutgoingInternalTransfer.execute(transfer);
            }

            pageable = pageable.next();

        } while (page.hasNext());
    }
}
