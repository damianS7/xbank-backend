package com.damian.xBank.modules.banking.transaction.infra.exception;

import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionAuthorizationException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionOwnershipException;
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
public class BankingTransactionExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BankingTransactionExceptionHandler.class);

    @ExceptionHandler(BankingTransactionOwnershipException.class)
    public ResponseEntity<ApiResponse<String>> handleOwnershipException(BankingTransactionOwnershipException ex) {
        log.warn(
                "Unauthorized access to Transaction {} from customer: {}",
                ex.getId(),
                ex.getCustomerId()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(BankingTransactionNotFoundException.class)
    public ResponseEntity<?> handleNotFound(BankingTransactionNotFoundException e) {
        log.warn("Transaction: {} not found", e.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(BankingTransactionException.class)
    public ResponseEntity<ApiResponse<String>> handleException(BankingTransactionException ex) {
        log.warn("Transaction: {} internal error.", ex.getId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BankingTransactionAuthorizationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(BankingTransactionAuthorizationException ex) {
        log.warn("Transaction: {} authorization exception.", ex.getId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }
}