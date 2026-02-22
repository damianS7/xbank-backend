package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferConfirmRequest;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferType;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.controller.TransferAuthorizationNetworkHttpGateway;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.TransferAuthorizationNetworkRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response.TransferAuthorizationNetworkResponse;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingTransferConfirm {
    private final TransferAuthorizationNetworkHttpGateway transferAuthorizationNetworkHttpGateway;
    private final NotificationPublisher notificationPublisher;
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingTransferRepository bankingTransferRepository;
    private final NotificationEventFactory notificationEventFactory;

    public BankingTransferConfirm(
        TransferAuthorizationNetworkHttpGateway transferAuthorizationNetworkHttpGateway,
        NotificationPublisher notificationPublisher,
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        BankingTransferRepository bankingTransferRepository,
        NotificationEventFactory notificationEventFactory
    ) {
        this.transferAuthorizationNetworkHttpGateway = transferAuthorizationNetworkHttpGateway;
        this.notificationPublisher = notificationPublisher;
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingTransferRepository = bankingTransferRepository;
        this.notificationEventFactory = notificationEventFactory;
    }

    @Transactional
    public BankingTransfer execute(
        Long transferId,
        BankingTransferConfirmRequest request
    ) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // validate customer password
        passwordValidator.validatePassword(currentUser, request.password());

        BankingTransfer transfer = bankingTransferRepository.findById(transferId).orElseThrow(
            () -> new BankingTransferNotFoundException(transferId)
        );

        // assert that the transfer belongs to userId
        transfer.assertOwnedBy(currentUser.getId());

        // confirm transfer
        transfer.confirm();

        // Save accounts (.save is optional because of transactional)
        // Saving the accounts also updates the transactions since we are using CASCADE.ALL
        bankingAccountRepository.save(transfer.getFromAccount());
        bankingAccountRepository.save(transfer.getToAccount());

        // Save transfer
        bankingTransferRepository.save(transfer);

        // Notify sender
        notificationPublisher.publish(
            notificationEventFactory.transferSent(transfer)
        );

        if (transfer.getType() == BankingTransferType.INTERNAL) {
            // Notify recipient
            notificationPublisher.publish(
                notificationEventFactory.transferReceived(transfer)
            );
        }

        if (transfer.getType() == BankingTransferType.EXTERNAL) {
            // TODO send transfer to RabbitMq queue for processing by external transfer service
            TransferAuthorizationNetworkResponse response = transferAuthorizationNetworkHttpGateway
                .authorizeTransfer(
                    new TransferAuthorizationNetworkRequest(
                        transfer.getFromAccount().getAccountNumber(),
                        transfer.getToAccountIban(),
                        transfer.getAmount(),
                        transfer.getFromAccount().getCurrency().name(),
                        transfer.getDescription()
                    )
                );
            transfer.setProviderAuthorizationId(response.authorizationId());
        }

        return transfer;
    }
}