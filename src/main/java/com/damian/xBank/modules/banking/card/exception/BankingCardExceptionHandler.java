package com.damian.xBank.modules.banking.card.exception;

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
public class BankingCardExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BankingCardExceptionHandler.class);

    @ExceptionHandler(BankingCardNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(BankingCardNotFoundException ex) {
        log.warn("Banking card: {} not found", ex.getBankingCardId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }


    @ExceptionHandler(BankingCardException.class)
    public ResponseEntity<ApiResponse<String>> handleApplicationException(BankingCardException ex) {
        log.warn("Banking card: {} internal error.", ex.getBankingCardId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BankingCardAuthorizationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(BankingCardAuthorizationException ex) {
        log.warn(
                "Unauthorized operation from customer {} on banking card: {}",
                ex.getCustomerId(),
                ex.getBankingCardId()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }
}