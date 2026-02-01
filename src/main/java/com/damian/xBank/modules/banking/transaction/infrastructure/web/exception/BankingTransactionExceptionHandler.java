package com.damian.xBank.modules.banking.transaction.infrastructure.web.exception;

import com.damian.xBank.modules.banking.transaction.domain.exception.*;
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
public class BankingTransactionExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(BankingTransactionExceptionHandler.class);
    private final MessageSource messageSource;

    public BankingTransactionExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BankingTransactionNotAuthorizedStatusException.class)
    public ResponseEntity<?> handleException(BankingTransactionNotAuthorizedStatusException ex) {
        log.warn("Transaction not authorized");

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(BankingTransactionStatusTransitionException.class)
    public ResponseEntity<?> handleException(BankingTransactionStatusTransitionException ex) {
        log.warn("Transaction status transition failed from {} to {}", ex.getArgs()[0], ex.getArgs()[1]);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingTransactionNotOwnerException.class)
    public ResponseEntity<ApiResponse<String>> handleOwnershipException(BankingTransactionNotOwnerException ex) {
        log.warn(
                "Unauthorized access to Transaction {} from customer: {}",
                ex.getResourceId(),
                ex.getArgs()[0]
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(BankingTransactionNotFoundException.class)
    public ResponseEntity<?> handleNotFound(BankingTransactionNotFoundException ex) {
        log.warn("Transaction: {} not found", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(BankingTransactionException.class)
    public ResponseEntity<ApiResponse<String>> handleException(BankingTransactionException ex) {
        log.warn("Transaction: {} internal error.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex, HttpStatus.INTERNAL_SERVER_ERROR, messageSource));
    }
}