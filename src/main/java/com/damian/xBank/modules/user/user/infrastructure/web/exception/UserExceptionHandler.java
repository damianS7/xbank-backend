package com.damian.xBank.modules.user.user.infrastructure.web.exception;

import com.damian.xBank.modules.user.user.domain.exception.*;
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
public class UserExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UserExceptionHandler.class);
    private final MessageSource messageSource;

    public UserExceptionHandler(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    // UserAccount exceptions
    @ExceptionHandler(UserNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleUserAccountNotFound(
            UserNotFoundException ex
    ) {
        log.warn(
                "user account: {} not found.",
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(UserEmailTakenException.class) // Handle conflict (409)
    public ResponseEntity<ApiResponse<String>> handleEmailAlreadyTaken(UserEmailTakenException ex) {
        log.warn("email: {} is already taken.", ex.getResourceId(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(UserVerificationNotPendingException.class) // Handle conflict (409)
    public ResponseEntity<ApiResponse<String>> handleAccountVerificationNotPending(
            UserVerificationNotPendingException ex
    ) {
        log.warn("account: {} is not pending for verification.", ex.getResourceId());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }


    @ExceptionHandler(UserInvalidPasswordConfirmationException.class) // 403
    public ResponseEntity<ApiResponse<String>> handlAccountInvalidPasswordConfirmation(
            UserInvalidPasswordConfirmationException ex
    ) {
        log.warn("user: {} failed to confirm password.", ex.getResourceId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(UserUpdateException.class) // 400
    public ResponseEntity<ApiResponse<String>> handleUserAccountUpdate(
            UserUpdateException ex
    ) {
        log.warn(
                "User id: {} failed to update field: {} with value: {}.",
                ex.getResourceId(),
                ex.getKey(),
                ex.getValue()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.BAD_REQUEST, messageSource));
    }
}