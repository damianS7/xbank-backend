package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.transfer.application.dto.request.BankingTransferRejectRequest;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.service.BankingTransferDomainService;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingTransferReject {
    private final BankingTransferRepository transferRepository;
    private final NotificationPublisher notificationPublisher;
    private final BankingTransferDomainService bankingTransferDomainService;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public BankingTransferReject(
            BankingTransferRepository transferRepository,
            NotificationPublisher notificationPublisher,
            BankingTransferDomainService bankingTransferDomainService,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator
    ) {
        this.transferRepository = transferRepository;
        this.notificationPublisher = notificationPublisher;
        this.bankingTransferDomainService = bankingTransferDomainService;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
    }

    /**
     * Reject a transfer
     *
     * @param transferId the id of the transfer
     * @param request    the request
     * @return the rejected transfer
     */
    @Transactional
    public BankingTransfer execute(
            Long transferId,
            BankingTransferRejectRequest request
    ) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // validate customer password
        passwordValidator.validatePassword(currentUser, request.password());

        // find the transfer
        BankingTransfer transfer = transferRepository.findById(transferId).orElseThrow(
                () -> new BankingTransferNotFoundException(transferId)
        );

        // reject
        bankingTransferDomainService.reject(currentUser.getId(), transfer);

        // No need for .save
        // Save accounts (.save is optional because of transactional)
        // Saving the accounts also updates the transactions since we are using CASCADE.ALL
        // bankingAccountRepository.save(transfer.getFromAccount());
        // bankingAccountRepository.save(transfer.getToAccount());

        // Save
        // transferRepository.save(transfer);

        return transfer;
    }

}