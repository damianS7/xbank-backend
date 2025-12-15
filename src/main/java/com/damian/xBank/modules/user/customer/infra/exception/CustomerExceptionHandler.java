package com.damian.xBank.modules.user.customer.infra.exception;

import com.damian.xBank.modules.user.customer.domain.exception.CustomerImageNotFoundException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerUpdateAuthorizationException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerUpdateException;
import com.damian.xBank.shared.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class CustomerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomerExceptionHandler.class);

    // Customer exceptions
    @ExceptionHandler(CustomerUpdateAuthorizationException.class) // 401
    public ResponseEntity<ApiResponse<String>> handleCustomerUpdateAuthorization(
            CustomerUpdateAuthorizationException ex
    ) {
        log.warn(
                "Customer id: {} cannot be updated due authorization violation.",
                ex.getCustomerId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(CustomerUpdateException.class) // 400
    public ResponseEntity<ApiResponse<String>> handleCustomerUpdate(
            CustomerUpdateException ex
    ) {
        log.warn(
                "Customer id: {} failed to update field: {} with value: {}.",
                ex.getCustomerId(),
                ex.getKey(),
                ex.getValue()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(CustomerNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleCustomerNotFound(
            CustomerNotFoundException ex
    ) {
        log.warn(
                "Customer id: {} not found.",
                ex.getCustomerId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(CustomerImageNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleCustomerImageNotFound(
            CustomerImageNotFoundException ex
    ) {
        log.warn(
                "Customer id: {} image not found.",
                ex.getCustomerId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }
}