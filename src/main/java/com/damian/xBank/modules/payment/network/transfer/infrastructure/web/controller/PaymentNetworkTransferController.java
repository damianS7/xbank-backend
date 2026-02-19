package com.damian.xBank.modules.payment.network.transfer.infrastructure.web.controller;

import com.damian.xBank.modules.payment.network.transfer.application.dto.request.AuthorizeIncomingTransferRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.IncomingTransferAuthorizedRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.OutgoingTransferFailureRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.response.AuthorizeIncomingTransferResponse;
import com.damian.xBank.modules.payment.network.transfer.application.usecase.AuthorizeIncomingTransfer;
import com.damian.xBank.modules.payment.network.transfer.application.usecase.HandleIncomingTransferAuthorized;
import com.damian.xBank.modules.payment.network.transfer.application.usecase.HandleOutgoingTransferFailure;
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
public class PaymentNetworkTransferController {
    private final AuthorizeIncomingTransfer authorizeIncomingTransfer;
    private final HandleIncomingTransferAuthorized handleIncomingTransferAuthorized;
    private final HandleOutgoingTransferFailure handleOutgoingTransferFailure;

    public PaymentNetworkTransferController(
        AuthorizeIncomingTransfer authorizeIncomingTransfer,
        HandleIncomingTransferAuthorized handleIncomingTransferAuthorized,
        HandleOutgoingTransferFailure handleOutgoingTransferFailure
    ) {
        this.authorizeIncomingTransfer = authorizeIncomingTransfer;
        this.handleIncomingTransferAuthorized = handleIncomingTransferAuthorized;
        this.handleOutgoingTransferFailure = handleOutgoingTransferFailure;
    }

    @PostMapping("/webhooks/transfers/incoming/authorize")
    public ResponseEntity<?> authorizeIncomingTransfer(
        @RequestBody @Valid
        AuthorizeIncomingTransferRequest request
    ) {
        AuthorizeIncomingTransferResponse response = authorizeIncomingTransfer.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @PostMapping("/webhooks/transfers/incoming/authorized")
    public ResponseEntity<?> onIncomingTransferAuthorized(
        @RequestBody @Valid
        IncomingTransferAuthorizedRequest request
    ) {
        handleIncomingTransferAuthorized.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }

    @PostMapping("/webhooks/transfers/outgoing/failed")
    public ResponseEntity<?> onOutgoingTransferFailure(
        @RequestBody @Valid
        OutgoingTransferFailureRequest request
    ) {
        handleOutgoingTransferFailure.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}