package com.damian.xBank.modules.payment.intent.infrastructure.web.exception;

import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotFoundException;
import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotPendingException;
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
public class PaymentIntentExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(PaymentIntentExceptionHandler.class);
    private final MessageSource messageSource;

    public PaymentIntentExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(PaymentIntentNotPendingException.class)
    public ResponseEntity<ApiResponse<String>> handleException(PaymentIntentNotPendingException ex) {
        log.warn("Payment intent: {} not pending", ex.getResourceId());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(PaymentIntentNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleException(PaymentIntentNotFoundException ex) {
        log.warn("Payment intent: {} not found.", ex.getResourceId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }
}