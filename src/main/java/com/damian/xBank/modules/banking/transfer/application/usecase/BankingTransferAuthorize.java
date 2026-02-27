package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.application.TransferAuthorizationNetworkGateway;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferConfirmRequest;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferAuthorizationFailedException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferType;
import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.controller.TransferAuthorizationNetworkHttpGateway;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.TransferAuthorizationNetworkRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response.TransferAuthorizationNetworkResponse;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingTransferAuthorize {
    private static final Logger log = LoggerFactory.getLogger(BankingTransferAuthorize.class);
    private final TransferAuthorizationNetworkGateway transferAuthorizationNetworkGateway;
    private final NotificationPublisher notificationPublisher;
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingTransferRepository bankingTransferRepository;
    private final NotificationEventFactory notificationEventFactory;

    public BankingTransferAuthorize(
        TransferAuthorizationNetworkHttpGateway transferAuthorizationNetworkGateway,
        NotificationPublisher notificationPublisher,
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        BankingTransferRepository bankingTransferRepository,
        NotificationEventFactory notificationEventFactory
    ) {
        this.transferAuthorizationNetworkGateway = transferAuthorizationNetworkGateway;
        this.notificationPublisher = notificationPublisher;
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingTransferRepository = bankingTransferRepository;
        this.notificationEventFactory = notificationEventFactory;
    }

    @Transactional
    public BankingTransfer execute(Long transferId, BankingTransferConfirmRequest request) {
        log.debug("Enter BankingTransferConfirm with id: {}", transferId);
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // validate customer password
        passwordValidator.validatePassword(currentUser, request.password());

        BankingTransfer transfer = bankingTransferRepository.findById(transferId)
            .orElseThrow(
                () -> new BankingTransferNotFoundException(transferId)
            );

        // assert that the transfer belongs to userId
        transfer.assertOwnedBy(currentUser.getId());

        // authorize transfer
        transfer.authorized();

        if (transfer.getType() == BankingTransferType.EXTERNAL) {
            log.debug("Authorizing external transfer: {}", transferId);
            // TODO send transfer to RabbitMq queue for processing by external transfer service
            TransferAuthorizationNetworkResponse response = transferAuthorizationNetworkGateway
                .authorizeTransfer(
                    new TransferAuthorizationNetworkRequest(
                        transfer.getFromAccount().getAccountNumber(),
                        transfer.getToAccountIban(),
                        transfer.getAmount(),
                        transfer.getFromAccount().getCurrency().name(),
                        transfer.getDescription()
                    )
                );

            if (response.status() != TransferAuthorizationStatus.PENDING) {
                throw new BankingTransferAuthorizationFailedException(
                    transferId,
                    response.rejectionReason()
                );
            }

            transfer.setProviderAuthorizationId(response.authorizationId());
        }

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

        return bankingTransferRepository.save(transfer);
    }
}