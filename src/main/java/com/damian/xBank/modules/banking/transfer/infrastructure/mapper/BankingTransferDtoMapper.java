package com.damian.xBank.modules.banking.transfer.infrastructure.mapper;

import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.confirm.ConfirmOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.create.CreateOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.reject.RejectOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.ConfirmOutgoingTransferRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.CreateOutgoingTransferRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.RejectOutgoingTransferRequest;

public class BankingTransferDtoMapper {
    public static CreateOutgoingTransferCommand toCreateTransferCommand(CreateOutgoingTransferRequest request) {
        return new CreateOutgoingTransferCommand(
            request.fromAccountId(),
            request.toAccountNumber(),
            request.description(),
            request.amount()
        );
    }

    public static RejectOutgoingTransferCommand toRejectTransferCommand(
        Long transferId,
        RejectOutgoingTransferRequest request
    ) {
        return new RejectOutgoingTransferCommand(
            transferId,
            request.password()
        );
    }

    public static ConfirmOutgoingTransferCommand toAuthorizeOutgoingTransferCommand(
        Long transferId,
        ConfirmOutgoingTransferRequest request
    ) {
        return new ConfirmOutgoingTransferCommand(
            transferId,
            request.password()
        );
    }
}
