package com.damian.xBank.modules.banking.account.infrastructure.web.exception;

import com.damian.xBank.modules.banking.account.domain.exception.*;
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
public class BankingAccountExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(BankingAccountExceptionHandler.class);
    private final MessageSource messageSource;

    public BankingAccountExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BankingAccountStatusTransitionException.class)
    public ResponseEntity<ApiResponse<String>> handleStatusTransition(
            BankingAccountStatusTransitionException ex
    ) {
        log.warn(
                "Status transition failed from {} to {} on Banking account: {}",
                ex.getArgs()[0],
                ex.getArgs()[1],
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingAccountException.class)
    public ResponseEntity<ApiResponse<String>> handleBankingAccountException(BankingAccountException ex) {
        log.warn("Banking account: {} internal error.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex, HttpStatus.INTERNAL_SERVER_ERROR, messageSource));
    }

    @ExceptionHandler(BankingAccountSuspendedException.class)
    public ResponseEntity<ApiResponse<String>> handleBankingAccountSuspended(
            BankingAccountSuspendedException ex
    ) {
        log.warn("Banking account: {} is suspended.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(BankingAccountClosedException.class)
    public ResponseEntity<ApiResponse<String>> handleBankingAccountClosed(BankingAccountClosedException ex) {
        log.warn("Banking account: {} is closed.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingAccountDepositException.class)
    public ResponseEntity<ApiResponse<String>> handleDepositException(BankingAccountDepositException ex) {
        log.warn("Banking deposit failed into account {}", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingAccountNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(BankingAccountNotFoundException ex) {
        log.warn("Banking account: {} not found", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(BankingAccountInsufficientFundsException.class)
    public ResponseEntity<ApiResponse<String>> handleInsufficientFunds(BankingAccountInsufficientFundsException ex) {
        log.warn("Banking account: {} has insufficient funds.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingAccountCardsLimitException.class)
    public ResponseEntity<ApiResponse<String>> handleCardLimit(BankingAccountCardsLimitException ex) {
        log.warn("Banking account: {} card limit per account reached", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingAccountNotOwnerException.class)
    public ResponseEntity<ApiResponse<String>> handleNotOwner(BankingAccountNotOwnerException ex) {
        log.warn(
                "Unauthorized access from user {} on banking account: {}",
                ex.getResourceId(),
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }
}