package com.damian.xBank.modules.banking.transfer.application.scheduler;

import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize.AuthorizeOutgoingExternalTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize.AuthorizeOutgoingInternalTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.authorize.AuthorizeOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferType;
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
public class OutgoingTransferAuthorizationJob {

    private static final Logger log = LoggerFactory.getLogger(OutgoingTransferAuthorizationJob.class);
    private final BankingTransferRepository bankingTransferRepository;
    private final AuthorizeOutgoingInternalTransfer authorizeOutgoingInternalTransfer;
    private final AuthorizeOutgoingExternalTransfer authorizeOutgoingExternalTransfer;

    public OutgoingTransferAuthorizationJob(
        BankingTransferRepository bankingTransferRepository,
        AuthorizeOutgoingInternalTransfer authorizeOutgoingInternalTransfer,
        AuthorizeOutgoingExternalTransfer authorizeOutgoingExternalTransfer
    ) {
        this.bankingTransferRepository = bankingTransferRepository;
        this.authorizeOutgoingInternalTransfer = authorizeOutgoingInternalTransfer;
        this.authorizeOutgoingExternalTransfer = authorizeOutgoingExternalTransfer;
    }

    /**
     *
     */
    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void authorizeTransfers() {
        Pageable pageable = PageRequest.of(0, 100);
        Page<BankingTransfer> page;

        do {
            page = bankingTransferRepository.findAllByStatus(
                BankingTransferStatus.CONFIRMED,
                pageable
            );

            for (BankingTransfer transfer : page.getContent()) {
                log.debug("Authorizing transfer: {}", transfer.toString());
                AuthorizeOutgoingTransferCommand command = new AuthorizeOutgoingTransferCommand(transfer.getId());
                if (transfer.getType() == BankingTransferType.INTERNAL) {
                    authorizeOutgoingInternalTransfer.execute(command);
                }

                if (transfer.getType() == BankingTransferType.EXTERNAL) {
                    authorizeOutgoingExternalTransfer.execute(command);
                }
            }

            pageable = pageable.next();

        } while (page.hasNext());
    }
}
