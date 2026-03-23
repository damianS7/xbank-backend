package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.get;

import com.damian.xBank.modules.banking.transfer.outgoing.application.dto.OutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository.OutgoingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class GetOutgoingTransfers {
    private final AuthenticationContext authenticationContext;
    private final OutgoingTransferRepository outgoingTransferRepository;

    public GetOutgoingTransfers(
        AuthenticationContext authenticationContext,
        OutgoingTransferRepository outgoingTransferRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.outgoingTransferRepository = outgoingTransferRepository;
    }

    public GetOutgoingTransfersResult execute(GetOutgoingTransfersQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        Page<OutgoingTransfer> paginatedTransfers = outgoingTransferRepository
            .findAllByFromAccount_UserId(currentUser.getId(), query.pageable());

        return GetOutgoingTransfersResult.from(paginatedTransfers.map(
            OutgoingTransferResult::from
        ));
    }
}