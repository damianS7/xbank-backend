package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.confirm.ConfirmOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.confirm.ConfirmOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.confirm.ConfirmOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create.CreateOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create.CreateOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create.CreateOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.get.GetOutgoingTransfers;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.get.GetOutgoingTransfersQuery;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.get.GetOutgoingTransfersResult;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.reject.RejectOutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.reject.RejectOutgoingTransferCommand;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.reject.RejectOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.mapper.OutgoingTransferDtoMapper;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.ConfirmOutgoingTransferRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.CreateOutgoingTransferRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.RejectOutgoingTransferRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class OutgoingTransferController {
    private final GetOutgoingTransfers getOutgoingTransfers;
    private final CreateOutgoingTransfer createOutgoingTransfer;
    private final ConfirmOutgoingTransfer confirmOutgoingTransfer;
    private final RejectOutgoingTransfer rejectOutgoingTransfer;


    public OutgoingTransferController(
        GetOutgoingTransfers getOutgoingTransfers,
        CreateOutgoingTransfer createOutgoingTransfer,
        ConfirmOutgoingTransfer confirmOutgoingTransfer,
        RejectOutgoingTransfer rejectOutgoingTransfer
    ) {
        this.getOutgoingTransfers = getOutgoingTransfers;
        this.createOutgoingTransfer = createOutgoingTransfer;
        this.confirmOutgoingTransfer = confirmOutgoingTransfer;
        this.rejectOutgoingTransfer = rejectOutgoingTransfer;
    }

    // endpoint to get all transfers from current user
    @GetMapping("/banking/transfers")
    public ResponseEntity<?> getTransfers(
        @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        GetOutgoingTransfersQuery query = new GetOutgoingTransfersQuery(pageable);
        GetOutgoingTransfersResult result = getOutgoingTransfers.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result.paginatedTransfers());
    }

    // endpoint to submit a transfer request
    @PostMapping("/banking/transfers")
    public ResponseEntity<?> transfer(
        @RequestBody @Valid
        CreateOutgoingTransferRequest request
    ) {
        CreateOutgoingTransferCommand command = OutgoingTransferDtoMapper.toCreateTransferCommand(request);
        CreateOutgoingTransferResult result = createOutgoingTransfer.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }

    @PostMapping("/banking/transfers/{id}/confirm")
    public ResponseEntity<?> confirm(
        @Positive @PathVariable
        Long id,
        @RequestBody @Valid
        ConfirmOutgoingTransferRequest request
    ) {
        ConfirmOutgoingTransferCommand command = OutgoingTransferDtoMapper.
            toAuthorizeOutgoingTransferCommand(id, request);

        ConfirmOutgoingTransferResult result = confirmOutgoingTransfer.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    @PostMapping("/banking/transfers/{id}/reject")
    public ResponseEntity<?> reject(
        @Positive @PathVariable
        Long id,
        @RequestBody @Valid
        RejectOutgoingTransferRequest request
    ) {
        RejectOutgoingTransferCommand command = OutgoingTransferDtoMapper.toRejectTransferCommand(id, request);
        RejectOutgoingTransferResult result = rejectOutgoingTransfer.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }
}