package com.damian.xBank.modules.banking.card.infrastructure.web.exception;

import com.damian.xBank.modules.banking.card.domain.exception.*;
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
public class BankingCardExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(BankingCardExceptionHandler.class);
    private final MessageSource messageSource;

    public BankingCardExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BankingCardStatusTransitionException.class)
    public ResponseEntity<ApiResponse<String>> handleStatusTransition(BankingCardStatusTransitionException ex) {
        log.warn("Banking card: {} status transition failed.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingCardLockedException.class)
    public ResponseEntity<ApiResponse<String>> handleLocked(BankingCardLockedException ex) {
        log.warn("Banking card: {} is locked.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingCardDisabledException.class)
    public ResponseEntity<ApiResponse<String>> handleDisabled(BankingCardDisabledException ex) {
        log.warn("Banking card: {} is disabled.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingCardInvalidPinException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidPin(BankingCardInvalidPinException ex) {
        log.warn("Banking card: {} pin is invalid.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(BankingCardInvalidCvvException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidPin(BankingCardInvalidCvvException ex) {
        log.warn("Banking card: {} cvv is invalid.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(BankingCardInsufficientFundsException.class)
    public ResponseEntity<ApiResponse<String>> handleInsufficientFunds(BankingCardInsufficientFundsException ex) {
        log.warn("Banking card: {} has insufficient funds.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingCardNotOwnerException.class)
    public ResponseEntity<ApiResponse<String>> handleOwnershipException(BankingCardNotOwnerException ex) {
        log.warn(
                "Unauthorized access from user {} on banking card: {}",
                ex.getArgs()[0],
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(BankingCardNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(BankingCardNotFoundException ex) {
        log.warn("Banking card: {} not found", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }


    @ExceptionHandler(BankingCardException.class)
    public ResponseEntity<ApiResponse<String>> handleApplicationException(BankingCardException ex) {
        log.warn("Banking card: {} internal error.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex, HttpStatus.INTERNAL_SERVER_ERROR, messageSource));
    }

    @ExceptionHandler(BankingCardAuthorizationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(BankingCardAuthorizationException ex) {
        log.warn(
                "Unauthorized operation from customer {} on banking card: {}",
                ex.getCustomerId(),
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }
}