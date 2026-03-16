package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.confirm;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for confirming an outgoing transfer.
 */
@Service
public class ConfirmOutgoingTransfer {
    private static final Logger log = LoggerFactory.getLogger(ConfirmOutgoingTransfer.class);
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final OutgoingTransferRepository outgoingTransferRepository;

    public ConfirmOutgoingTransfer(
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        OutgoingTransferRepository outgoingTransferRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.outgoingTransferRepository = outgoingTransferRepository;
    }

    @Transactional
    public ConfirmOutgoingTransferResult execute(ConfirmOutgoingTransferCommand command) {
        log.debug("Enter BankingTransferConfirm with id: {}", command.transferId());
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // validate customer password
        passwordValidator.validatePassword(currentUser, command.password());

        OutgoingTransfer transfer = outgoingTransferRepository.findById(command.transferId())
            .orElseThrow(
                () -> new OutgoingTransferNotFoundException(command.transferId())
            );

        // assert that the transfer belongs to userId
        transfer.assertOwnedBy(currentUser.getId());

        // confirm transfer
        transfer.confirm();

        outgoingTransferRepository.save(transfer);

        return ConfirmOutgoingTransferResult.from(transfer);
    }
}