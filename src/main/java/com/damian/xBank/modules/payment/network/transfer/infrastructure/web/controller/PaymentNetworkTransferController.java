package com.damian.xBank.modules.payment.network.transfer.infrastructure.web.controller;

import com.damian.xBank.modules.payment.network.transfer.AuthorizeIncomingTransfer;
import com.damian.xBank.modules.payment.network.transfer.IncomingTransfer;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.IncomingTransferAuthorizationRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.IncomingTransferRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.response.IncomingTransferAuthorizationResponse;
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
    private final IncomingTransfer incomingTransfer;

    public PaymentNetworkTransferController(
        AuthorizeIncomingTransfer authorizeIncomingTransfer,
        IncomingTransfer incomingTransfer
    ) {
        this.authorizeIncomingTransfer = authorizeIncomingTransfer;
        this.incomingTransfer = incomingTransfer;
    }

    @PostMapping("/transfers/incoming/authorize")
    public ResponseEntity<?> authorizeIncomingTransfer(
        @RequestBody @Valid
        IncomingTransferAuthorizationRequest request
    ) {
        IncomingTransferAuthorizationResponse response = authorizeIncomingTransfer.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @PostMapping("/transfers/incoming/execute")
    public ResponseEntity<?> incomingTransfer(
        @RequestBody @Valid
        IncomingTransferRequest request
    ) {
        incomingTransfer.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }

    @PostMapping("/transfers/incoming/notify")
    public ResponseEntity<?> notifyFailure(
        @RequestBody @Valid
        IncomingTransferRequest request
    ) {
        incomingTransfer.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}