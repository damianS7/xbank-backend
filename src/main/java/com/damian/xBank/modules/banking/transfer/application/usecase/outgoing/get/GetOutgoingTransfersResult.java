package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.get;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import org.springframework.data.domain.Page;

public record GetOutgoingTransfersResult(
    Page<BankingTransfer> paginatedTransfers
) {
    public static GetOutgoingTransfersResult from(Page<BankingTransfer> paginatedTransfers) {
        return new GetOutgoingTransfersResult(
            paginatedTransfers
        );
    }
}
