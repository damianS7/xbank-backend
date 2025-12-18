package com.damian.xBank.modules.user.customer.infra.exception;

import com.damian.xBank.modules.user.customer.domain.exception.CustomerImageNotFoundException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerUpdateAuthorizationException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerUpdateException;
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
public class CustomerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomerExceptionHandler.class);
    private final MessageSource messageSource;

    public CustomerExceptionHandler(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    // Customer exceptions
    @ExceptionHandler(CustomerUpdateAuthorizationException.class) // 401
    public ResponseEntity<ApiResponse<String>> handleCustomerUpdateAuthorization(
            CustomerUpdateAuthorizationException ex
    ) {
        log.warn(
                "Customer id: {} cannot be updated due authorization violation.",
                ex.getResourceId()
        );

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(message, HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(CustomerUpdateException.class) // 400
    public ResponseEntity<ApiResponse<String>> handleCustomerUpdate(
            CustomerUpdateException ex
    ) {
        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        log.warn(
                "Customer id: {} failed to update field: {} with value: {}.",
                ex.getResourceId(),
                ex.getArgs()[0],
                ex.getArgs()[1]
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(CustomerNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleCustomerNotFound(
            CustomerNotFoundException ex
    ) {
        log.warn(
                "Customer id: {} not found.",
                ex.getResourceId()
        );

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(message, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(CustomerImageNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleCustomerImageNotFound(
            CustomerImageNotFoundException ex
    ) {
        log.warn(
                "Customer id: {} image not found.",
                ex.getResourceId()
        );

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(message, HttpStatus.NOT_FOUND));
    }
}