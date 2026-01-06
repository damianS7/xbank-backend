package com.damian.xBank.modules.user.profile.infrastructure.web.exception;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileImageNotFoundException;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotOwnerException;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileUpdateException;
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
public class UserProfileExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UserProfileExceptionHandler.class);
    private final MessageSource messageSource;

    public UserProfileExceptionHandler(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    // Customer exceptions
    @ExceptionHandler(UserProfileNotOwnerException.class) // 401
    public ResponseEntity<ApiResponse<String>> handleCustomerUpdateAuthorization(
            UserProfileNotOwnerException ex
    ) {
        log.warn(
                "Customer id: {} cannot be updated due authorization violation.",
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.UNAUTHORIZED, messageSource));
    }

    @ExceptionHandler(UserProfileUpdateException.class) // 400
    public ResponseEntity<ApiResponse<String>> handleCustomerUpdate(
            UserProfileUpdateException ex
    ) {

        log.warn(
                "Customer id: {} failed to update field: {} with value: {}.",
                ex.getResourceId(),
                ex.getArgs()[0],
                ex.getArgs()[1]
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.BAD_REQUEST, messageSource));
    }

    @ExceptionHandler(UserProfileNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleCustomerNotFound(
            UserProfileNotFoundException ex
    ) {
        log.warn(
                "Customer id: {} not found.",
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(UserProfileImageNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleCustomerImageNotFound(
            UserProfileImageNotFoundException ex
    ) {
        log.warn(
                "Customer id: {} image not found.",
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }
}