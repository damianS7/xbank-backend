package com.damian.xBank.modules.banking.transfer.infrastructure.web.controller;

import com.damian.xBank.modules.banking.transfer.application.usecase.HandleOutgoingTransferAuthorizationFailure;
import com.damian.xBank.modules.banking.transfer.application.usecase.ProcessIncomingTransfer;
import com.damian.xBank.modules.banking.transfer.application.usecase.ProcessIncomingTransferAuthorization;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.IncomingTransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.IncomingTransferRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.OutgoingTransferFailureRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response.IncomingTransferAuthorizationResponse;
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
    private final ProcessIncomingTransferAuthorization processIncomingTransferAuthorization;
    private final ProcessIncomingTransfer processIncomingTransfer;
    private final HandleOutgoingTransferAuthorizationFailure handleOutgoingTransferAuthorizationFailure;

    public TransferAuthorizationNetworkController(
        ProcessIncomingTransferAuthorization processIncomingTransferAuthorization,
        ProcessIncomingTransfer processIncomingTransfer,
        HandleOutgoingTransferAuthorizationFailure handleOutgoingTransferAuthorizationFailure
    ) {
        this.processIncomingTransferAuthorization = processIncomingTransferAuthorization;
        this.processIncomingTransfer = processIncomingTransfer;
        this.handleOutgoingTransferAuthorizationFailure = handleOutgoingTransferAuthorizationFailure;
    }

    @PostMapping("/webhooks/transfers/authorize")
    public ResponseEntity<?> authorizeIncomingTransfer(
        @RequestBody @Valid
        IncomingTransferAuthorizationRequest request
    ) {
        IncomingTransferAuthorizationResponse response = processIncomingTransferAuthorization.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @PostMapping("/webhooks/transfers/incoming")
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