package com.damian.xBank.modules.banking.payment.infrastructure.web.controller;

import com.damian.xBank.modules.banking.payment.application.dto.mapper.PaymentDtoMapper;
import com.damian.xBank.modules.banking.payment.application.dto.request.PaymentCreateRequest;
import com.damian.xBank.modules.banking.payment.application.dto.response.PaymentDto;
import com.damian.xBank.modules.banking.payment.application.usecase.PaymentCreate;
import com.damian.xBank.modules.banking.payment.domain.model.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class PaymentMerchantController {
    private final PaymentCreate paymentCreate;

    public PaymentMerchantController(
            PaymentCreate paymentCreate
    ) {
        this.paymentCreate = paymentCreate;
    }

    // TODO
    @PostMapping("/payments")
    public ResponseEntity<?> transfer(
            @RequestBody @Validated
            PaymentCreateRequest request
    ) {
        Payment payment = paymentCreate.execute(request);
        PaymentDto paymentDto = PaymentDtoMapper.toPaymentDto(payment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentDto);
    }
}