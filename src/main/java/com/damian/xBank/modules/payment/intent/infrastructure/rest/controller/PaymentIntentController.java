package com.damian.xBank.modules.payment.intent.infrastructure.rest.controller;

import com.damian.xBank.modules.payment.intent.application.usecase.create.CreatePaymentIntent;
import com.damian.xBank.modules.payment.intent.application.usecase.create.CreatePaymentIntentCommand;
import com.damian.xBank.modules.payment.intent.application.usecase.create.CreatePaymentIntentResult;
import com.damian.xBank.modules.payment.intent.infrastructure.rest.request.CreatePaymentIntentRequest;
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
public class PaymentIntentController {
    private final CreatePaymentIntent createPaymentIntent;

    public PaymentIntentController(
        CreatePaymentIntent createPaymentIntent
    ) {
        this.createPaymentIntent = createPaymentIntent;
    }

    /**
     * Endpoint para que un merchant asociado cree payment intents
     *
     * @param request
     * @return El payment intent creado
     */
    @PostMapping("/payment-intents")
    public ResponseEntity<?> createPaymentIntent(
        @RequestBody @Valid
        CreatePaymentIntentRequest request
    ) {
        CreatePaymentIntentCommand command = new CreatePaymentIntentCommand(
            request.orderId(),
            request.description(),
            request.amount(),
            request.currency()
        );

        CreatePaymentIntentResult result = createPaymentIntent.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }
}