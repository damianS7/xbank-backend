package com.damian.xBank.modules.banking.payment.infrastructure.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class PaymentExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(PaymentExceptionHandler.class);
    private final MessageSource messageSource;

    public PaymentExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


}