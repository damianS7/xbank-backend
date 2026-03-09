package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.reject;

import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RejectOutgoingTransfer {
    private final BankingTransferRepository transferRepository;
    private final NotificationPublisher notificationPublisher;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public RejectOutgoingTransfer(
        BankingTransferRepository transferRepository,
        NotificationPublisher notificationPublisher,
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator
    ) {
        this.transferRepository = transferRepository;
        this.notificationPublisher = notificationPublisher;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
    }

    /**
     * Reject a transfer
     *
     * @param command the command containing the transfer ID and the user's password for validation
     * @return the rejected transfer
     */
    @Transactional
    public RejectOutgoingTransferResult execute(RejectOutgoingTransferCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // validate customer password
        passwordValidator.validatePassword(currentUser, command.password());

        // find the transfer
        BankingTransfer transfer = transferRepository.findById(command.transferId())
            .orElseThrow(
                () -> new BankingTransferNotFoundException(command.transferId())
            );

        // assert owner
        transfer.assertOwnedBy(currentUser.getId());

        // reject
        transfer.reject("Rejected by user.");

        // No need for .save
        // Save accounts (.save is optional because of transactional)
        // Saving the accounts also updates the transactions since we are using CASCADE.ALL
        // bankingAccountRepository.save(transfer.getFromAccount());
        // bankingAccountRepository.save(transfer.getToAccount());

        // Save
        // transferRepository.save(transfer);

        return RejectOutgoingTransferResult.from(transfer);
    }

}