package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.confirm;

import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
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
    private final BankingTransferRepository bankingTransferRepository;

    public ConfirmOutgoingTransfer(
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        BankingTransferRepository bankingTransferRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingTransferRepository = bankingTransferRepository;
    }

    @Transactional
    public ConfirmOutgoingTransferResult execute(ConfirmOutgoingTransferCommand command) {
        log.debug("Enter BankingTransferConfirm with id: {}", command.transferId());
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // validate customer password
        passwordValidator.validatePassword(currentUser, command.password());

        BankingTransfer transfer = bankingTransferRepository.findById(command.transferId())
            .orElseThrow(
                () -> new BankingTransferNotFoundException(command.transferId())
            );

        // assert that the transfer belongs to userId
        transfer.assertOwnedBy(currentUser.getId());

        // confirm transfer
        transfer.confirm();

        bankingTransferRepository.save(transfer);

        return ConfirmOutgoingTransferResult.from(transfer);
    }
}