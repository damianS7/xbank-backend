package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.get;

import com.damian.xBank.modules.banking.transfer.application.dto.BankingTransferResult;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.infrastructure.repository.BankingTransferRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class GetOutgoingTransfers {
    private final AuthenticationContext authenticationContext;
    private final BankingTransferRepository bankingTransferRepository;

    public GetOutgoingTransfers(
        AuthenticationContext authenticationContext,
        BankingTransferRepository bankingTransferRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingTransferRepository = bankingTransferRepository;
    }

    public GetOutgoingTransfersResult execute(GetOutgoingTransfersQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        Page<BankingTransfer> paginatedTransfers = bankingTransferRepository
            .findAllByFromAccount_UserId(currentUser.getId(), query.pageable());

        return GetOutgoingTransfersResult.from(paginatedTransfers.map(
            BankingTransferResult::from
        ));
    }
}