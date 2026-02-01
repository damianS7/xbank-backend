package com.damian.xBank.modules.payment.checkout.infrastructure.exception;

import com.damian.xBank.modules.payment.checkout.domain.excepcion.PaymentCheckoutException;
import com.damian.xBank.shared.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class PaymentCheckoutExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(PaymentCheckoutExceptionHandler.class);

    private final MessageSource messageSource;

    public PaymentCheckoutExceptionHandler(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(PaymentCheckoutException.class)
    public ResponseEntity<ApiResponse<String>> handleException(PaymentCheckoutException ex) {
        log.warn("Payment checkout: {} exception.", ex.getResourceId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex, HttpStatus.INTERNAL_SERVER_ERROR, messageSource));
    }
}