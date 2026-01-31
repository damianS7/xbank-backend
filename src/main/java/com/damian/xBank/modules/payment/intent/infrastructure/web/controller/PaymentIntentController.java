package com.damian.xBank.modules.payment.intent.infrastructure.web.controller;

import com.damian.xBank.modules.payment.intent.application.dto.mapper.PaymentIntentOrderDtoMapper;
import com.damian.xBank.modules.payment.intent.application.dto.request.CreatePaymentIntentRequest;
import com.damian.xBank.modules.payment.intent.application.dto.response.PaymentOrderDto;
import com.damian.xBank.modules.payment.intent.application.usecase.CreatePaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
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
     * Endpoint for merchants to create payment intents
     *
     * @param request
     * @return the created payment intent
     */
    @PostMapping("/payment-intents")
    public ResponseEntity<?> createPaymentIntent(
            @RequestBody @Valid
            CreatePaymentIntentRequest request
    ) {
        PaymentIntent paymentIntent = createPaymentIntent.execute(request);
        PaymentOrderDto paymentOrderDto = PaymentIntentOrderDtoMapper.toPaymentDto(paymentIntent);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentOrderDto);
    }
}