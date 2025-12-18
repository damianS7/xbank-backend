package com.damian.xBank.modules.banking.card.infra.exception;

import com.damian.xBank.modules.banking.card.domain.exception.*;
import com.damian.xBank.shared.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingCardLockedException.class)
    public ResponseEntity<ApiResponse<String>> handleLocked(BankingCardLockedException ex) {
        log.warn("Banking card: {} is locked.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingCardDisabledException.class)
    public ResponseEntity<ApiResponse<String>> handleDisabled(BankingCardDisabledException ex) {
        log.warn("Banking card: {} is disabled.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingCardInvalidPinException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidPin(BankingCardInvalidPinException ex) {
        log.warn("Banking card: {} pin is invalid.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(message, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(BankingCardInsufficientFundsException.class)
    public ResponseEntity<ApiResponse<String>> handleInsufficientFunds(BankingCardInsufficientFundsException ex) {
        log.warn("Banking card: {} has insufficient funds.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingCardOwnershipException.class)
    public ResponseEntity<ApiResponse<String>> handleOwnershipException(BankingCardOwnershipException ex) {
        log.warn(
                "Unauthorized access from user {} on banking card: {}",
                ex.getArgs()[0],
                ex.getResourceId()
        );

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(message, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(BankingCardNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(BankingCardNotFoundException ex) {
        log.warn("Banking card: {} not found", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(message, HttpStatus.NOT_FOUND));
    }


    @ExceptionHandler(BankingCardException.class)
    public ResponseEntity<ApiResponse<String>> handleApplicationException(BankingCardException ex) {
        log.warn("Banking card: {} internal error.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(message, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BankingCardAuthorizationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(BankingCardAuthorizationException ex) {
        log.warn(
                "Unauthorized operation from customer {} on banking card: {}",
                ex.getCustomerId(),
                ex.getResourceId()
        );

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(message, HttpStatus.FORBIDDEN));
    }
}