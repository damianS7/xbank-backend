package com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.controller;

import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize.AuthorizeIncomingTransfer;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize.AuthorizeIncomingTransferCommand;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.authorize.AuthorizeIncomingTransferResult;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.complete.CompleteIncomingTransfer;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.complete.CompleteIncomingTransferCommand;
import com.damian.xBank.modules.banking.transfer.incoming.application.usecase.complete.CompleteIncomingTransferResult;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.request.AuthorizeIncomingTransferRequest;
import com.damian.xBank.modules.banking.transfer.incoming.infrastructure.rest.request.CompleteIncomingTransferRequest;
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
public class IncomingTransferController {
    private final AuthorizeIncomingTransfer authorizeIncomingTransfer;
    private final CompleteIncomingTransfer completeIncomingTransfer;

    public IncomingTransferController(
        AuthorizeIncomingTransfer authorizeIncomingTransfer,
        CompleteIncomingTransfer completeIncomingTransfer
    ) {
        this.authorizeIncomingTransfer = authorizeIncomingTransfer;
        this.completeIncomingTransfer = completeIncomingTransfer;
    }

    /**
     * Autoriza una transferencia entrante
     *
     * @param request Petición con los datos de la transferencia
     * @return 200
     */
    @PostMapping("/webhooks/transfers/incoming/authorize")
    public ResponseEntity<?> authorize(
        @RequestBody @Valid
        AuthorizeIncomingTransferRequest request
    ) {
        AuthorizeIncomingTransferCommand command = new AuthorizeIncomingTransferCommand(
            request.authorizationId(),
            request.fromIban(),
            request.toIban(),
            request.amount(),
            request.currency(),
            request.reference()
        );

        AuthorizeIncomingTransferResult result = authorizeIncomingTransfer.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    /**
     * Completa una transferencia
     * TODO: quiza no es necesario???? Si ya me llego la peticion de autorizacion y tengo los datos???
     *
     * @param request Petición con los datos de la transferencia
     * @return 200
     */
    @PostMapping("/webhooks/transfers/incoming/complete")
    public ResponseEntity<?> complete(
        @RequestBody @Valid
        CompleteIncomingTransferRequest request
    ) {
        CompleteIncomingTransferCommand command = new CompleteIncomingTransferCommand(
            request.authorizationId()
        );

        CompleteIncomingTransferResult result = completeIncomingTransfer.execute(command);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }
}