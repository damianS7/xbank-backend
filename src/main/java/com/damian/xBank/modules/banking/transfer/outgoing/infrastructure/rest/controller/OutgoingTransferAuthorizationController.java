package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.fail.FailedOutgoingTransferResult;
import com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.fail.OutgoingTransferAuthorizationFailure;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.OutgoingTransferFailureRequest;
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
public class OutgoingTransferAuthorizationController {
    private final OutgoingTransferAuthorizationFailure outgoingTransferAuthorizationFailure;

    public OutgoingTransferAuthorizationController(
        OutgoingTransferAuthorizationFailure outgoingTransferAuthorizationFailure
    ) {
        this.outgoingTransferAuthorizationFailure = outgoingTransferAuthorizationFailure;
    }

    @PostMapping("/webhooks/transfers/outgoing/failed")
    public ResponseEntity<?> onOutgoingTransferFailure(
        @RequestBody @Valid
        OutgoingTransferFailureRequest request
    ) {
        FailedOutgoingTransferResult result = outgoingTransferAuthorizationFailure.execute(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }
}