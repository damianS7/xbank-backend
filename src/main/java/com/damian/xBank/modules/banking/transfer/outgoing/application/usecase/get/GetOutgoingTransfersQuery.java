package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.get;

import org.springframework.data.domain.Pageable;

public record GetOutgoingTransfersQuery(
    Pageable pageable
) {
}
