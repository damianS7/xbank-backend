package com.damian.xBank.modules.banking.account.exception;

import com.damian.xBank.modules.banking.card.exception.BankingAccountCardsLimitException;
import com.damian.xBank.shared.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class BankingAccountExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BankingAccountExceptionHandler.class);

    @ExceptionHandler(BankingAccountNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(BankingAccountNotFoundException ex) {
        log.warn("Banking account: {} not found", ex.getBankingAccountId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(BankingAccountInsufficientFundsException.class)
    public ResponseEntity<ApiResponse<String>> handleInsufficientFunds(BankingAccountInsufficientFundsException ex) {
        log.warn("Banking account: {} has insufficient funds.", ex.getBankingAccountId());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountCardsLimitException.class)
    public ResponseEntity<ApiResponse<String>> handleConflitException(BankingAccountCardsLimitException ex) {
        log.warn("Banking account: {} card limit per account reached", ex.getBankingAccountId());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BankingAccountException.class)
    public ResponseEntity<ApiResponse<String>> handleBankingAccountException(BankingAccountException ex) {
        log.warn("Banking account: {} internal error.", ex.getBankingAccountId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BankingAccountAuthorizationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(BankingAccountAuthorizationException ex) {
        log.warn(
                "Unauthorized operation from user {} on banking account: {}",
                ex.getCustomerId(),
                ex.getBankingAccountId()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }
}