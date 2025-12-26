package com.damian.xBank.modules.banking.transfer.infrastructure.exception;

import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotFoundException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferSameAccountException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferStatusTransitionException;
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
public class BankingTransferExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(BankingTransferExceptionHandler.class);
    private final MessageSource messageSource;

    public BankingTransferExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BankingTransferStatusTransitionException.class)
    public ResponseEntity<ApiResponse<String>> handleStatusTransition(
            BankingTransferStatusTransitionException ex
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

    @ExceptionHandler(BankingTransferNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(BankingTransferNotFoundException ex) {
        log.warn("Banking transfer: {} not found", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(BankingTransferSameAccountException.class)
    public ResponseEntity<ApiResponse<String>> handleTransferSameAccount(BankingTransferSameAccountException ex) {
        log.warn("Banking transfer failed because both accounts are the same.");

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(BankingTransferCurrencyMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleCurrencyMismatch(
            BankingTransferCurrencyMismatchException ex
    ) {
        log.warn(
                "Banking transfer failed due account destination {} has different a currency.",
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }
}