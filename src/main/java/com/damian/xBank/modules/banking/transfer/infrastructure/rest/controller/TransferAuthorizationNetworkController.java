package com.damian.xBank.modules.banking.transfer.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.incoming.ProcessIncomingTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.incoming.authorize.AuthorizeIncomingTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.incoming.authorize.AuthorizeIncomingTransferCommand;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.incoming.authorize.AuthorizeIncomingTransferResult;
import com.damian.xBank.modules.banking.transfer.application.usecase.transfer.outgoing.authorize.HandleOutgoingTransferAuthorizationFailure;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request.AuthorizeIncomingTransferRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request.IncomingTransferRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request.OutgoingTransferFailureRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class TransferAuthorizationNetworkController {
    private final AuthorizeIncomingTransfer authorizeIncomingTransfer;
    private final ProcessIncomingTransfer processIncomingTransfer;
    private final HandleOutgoingTransferAuthorizationFailure handleOutgoingTransferAuthorizationFailure;

    public TransferAuthorizationNetworkController(
        AuthorizeIncomingTransfer authorizeIncomingTransfer,
        ProcessIncomingTransfer processIncomingTransfer,
        HandleOutgoingTransferAuthorizationFailure handleOutgoingTransferAuthorizationFailure
    ) {
        this.authorizeIncomingTransfer = authorizeIncomingTransfer;
        this.processIncomingTransfer = processIncomingTransfer;
        this.handleOutgoingTransferAuthorizationFailure = handleOutgoingTransferAuthorizationFailure;
    }

    @PostMapping("/webhooks/transfers/incoming/authorize")
    public ResponseEntity<?> authorizeIncomingTransfer(
        @RequestBody @Valid
        AuthorizeIncomingTransferRequest request
    ) {
        AuthorizeIncomingTransferCommand command = new AuthorizeIncomingTransferCommand(
            request.toIban(),
            request.amount(),
            request.currency()
        );

        AuthorizeIncomingTransferResult result = authorizeIncomingTransfer.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    @PostMapping("/webhooks/transfers/incoming/authorized")
    public ResponseEntity<?> onIncomingTransfer(
        @RequestBody @Valid
        IncomingTransferRequest request
    ) {
        processIncomingTransfer.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }

    @PostMapping("/webhooks/transfers/outgoing/failed")
    public ResponseEntity<?> onOutgoingTransferFailure(
        @RequestBody @Valid
        OutgoingTransferFailureRequest request
    ) {
        handleOutgoingTransferAuthorizationFailure.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}