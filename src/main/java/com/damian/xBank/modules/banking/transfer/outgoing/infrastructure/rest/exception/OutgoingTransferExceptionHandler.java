package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.exception;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferAuthorizationFailedException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferNotOwnerException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferSameAccountException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferStatusTransitionException;
import com.damian.xBank.shared.infrastructure.web.dto.response.ApiResponse;
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
public class OutgoingTransferExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(OutgoingTransferExceptionHandler.class);
    private final MessageSource messageSource;

    public OutgoingTransferExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(OutgoingTransferNotOwnerException.class)
    public ResponseEntity<ApiResponse<String>> handleExcepcion(OutgoingTransferNotOwnerException ex) {
        log.warn("Banking transfer: {} not owner", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(ex, HttpStatus.UNAUTHORIZED, messageSource));
    }

    @ExceptionHandler(OutgoingTransferStatusTransitionException.class)
    public ResponseEntity<ApiResponse<String>> handleStatusTransition(
        OutgoingTransferStatusTransitionException ex
    ) {
        log.warn(
            "Status transition failed from {} to {} on Banking transfer: {}",
            ex.getArgs()[0],
            ex.getArgs()[1],
            ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(OutgoingTransferNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handle(OutgoingTransferNotFoundException ex) {
        log.warn("Banking transfer: {} not found", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(OutgoingTransferAuthorizationFailedException.class)
    public ResponseEntity<ApiResponse<String>> handle(OutgoingTransferAuthorizationFailedException ex) {
        log.warn("Banking transfer authorization failed: {} ", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(OutgoingTransferSameAccountException.class)
    public ResponseEntity<ApiResponse<String>> handleTransferSameAccount(OutgoingTransferSameAccountException ex) {
        log.warn("Banking transfer failed because both accounts are the same.");

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(OutgoingTransferCurrencyMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleCurrencyMismatch(
        OutgoingTransferCurrencyMismatchException ex
    ) {
        log.warn(
            "Banking transfer failed due account destination {} has different a currency.",
            ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }
}