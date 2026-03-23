package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.get;

import com.damian.xBank.modules.banking.transfer.outgoing.application.dto.OutgoingTransferResult;
import org.springframework.data.domain.Page;

public record GetOutgoingTransfersResult(
    Page<OutgoingTransferResult> paginatedTransfers
) {
    public static GetOutgoingTransfersResult from(Page<OutgoingTransferResult> paginatedTransfers) {
        return new GetOutgoingTransfersResult(
            paginatedTransfers
        );
    }
}
