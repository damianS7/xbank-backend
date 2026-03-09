package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.get;

import org.springframework.data.domain.Pageable;

public record GetOutgoingTransfersQuery(
    Pageable pageable
) {
}
