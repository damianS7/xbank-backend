package com.damian.xBank.modules.banking.account.infra.exception;

import com.damian.xBank.modules.banking.account.domain.exception.*;
import com.damian.xBank.modules.banking.card.domain.exception.BankingAccountCardsLimitException;
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
public class BankingAccountExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(BankingAccountExceptionHandler.class);
    private final MessageSource messageSource;

    public BankingAccountExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @ExceptionHandler(BankingAccountStatusTransitionException.class)
    public ResponseEntity<ApiResponse<String>> handleStatusTransition(BankingAccountStatusTransitionException ex) {
        log.warn("Banking account: {} status transition failed.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountException.class)
    public ResponseEntity<ApiResponse<String>> handleBankingAccountException(BankingAccountException ex) {
        log.warn("Banking account: {} internal error.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(message, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BankingAccountSuspendedException.class)
    public ResponseEntity<ApiResponse<String>> handleBankingAccountSuspended(BankingAccountSuspendedException ex) {
        log.warn("Banking account: {} is suspended.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(message, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(BankingAccountClosedException.class)
    public ResponseEntity<ApiResponse<String>> handleBankingAccountClosed(BankingAccountClosedException ex) {
        log.warn("Banking account: {} is closed.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountDepositException.class)
    public ResponseEntity<ApiResponse<String>> handleDepositException(BankingAccountDepositException ex) {
        log.warn("Banking deposit failed into account {}", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountTransferSameAccountException.class)
    public ResponseEntity<ApiResponse<String>> handleTransferSameAccount(BankingAccountTransferSameAccountException ex) {
        log.warn("Banking transfer failed because both accounts are the same.");

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountTransferCurrencyMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleCurrencyMismatch(BankingAccountTransferCurrencyMismatchException ex) {
        log.warn(
                "Banking transfer failed due account: {} and account: {} has different currencies.",
                ex.getResourceId(),
                ex.getToBankingAccountId()
        );

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(BankingAccountNotFoundException ex) {
        log.warn("Banking account: {} not found", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(message, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(BankingAccountInsufficientFundsException.class)
    public ResponseEntity<ApiResponse<String>> handleInsufficientFunds(BankingAccountInsufficientFundsException ex) {
        log.warn("Banking account: {} has insufficient funds.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountCardsLimitException.class)
    public ResponseEntity<ApiResponse<String>> handleConflitException(BankingAccountCardsLimitException ex) {
        log.warn("Banking account: {} card limit per account reached", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(message, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountOwnershipException.class)
    public ResponseEntity<ApiResponse<String>> handleOwnershipException(BankingAccountOwnershipException ex) {
        log.warn(
                "Unauthorized access from user {} on banking account: {}",
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

    @ExceptionHandler(BankingAccountAuthorizationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(BankingAccountAuthorizationException ex) {
        log.warn(
                "Unauthorized operation from user {} on banking account: {}",
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