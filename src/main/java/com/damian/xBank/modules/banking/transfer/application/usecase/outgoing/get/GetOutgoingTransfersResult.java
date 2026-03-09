package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.get;

import com.damian.xBank.modules.banking.transfer.application.dto.BankingTransferResult;
import org.springframework.data.domain.Page;

public record GetOutgoingTransfersResult(
    Page<BankingTransferResult> paginatedTransfers
) {
    public static GetOutgoingTransfersResult from(Page<BankingTransferResult> paginatedTransfers) {
        return new GetOutgoingTransfersResult(
            paginatedTransfers
        );
    }
}
