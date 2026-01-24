package com.damian.xBank.modules.payment.intent.infrastructure.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class PaymentIntentExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(PaymentIntentExceptionHandler.class);
    private final MessageSource messageSource;

    public PaymentIntentExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


}