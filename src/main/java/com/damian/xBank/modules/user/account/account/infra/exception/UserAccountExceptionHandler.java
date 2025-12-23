package com.damian.xBank.modules.user.account.account.infra.exception;

import com.damian.xBank.modules.user.account.account.domain.exception.*;
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
public class UserAccountExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UserAccountExceptionHandler.class);
    private final MessageSource messageSource;

    public UserAccountExceptionHandler(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    // UserAccount exceptions
    @ExceptionHandler(UserAccountNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleUserAccountNotFound(
            UserAccountNotFoundException ex
    ) {
        log.warn(
                "user account: {} not found.",
                ex.getResourceId()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(UserAccountEmailTakenException.class) // Handle conflict (409)
    public ResponseEntity<ApiResponse<String>> handleEmailAlreadyTaken(UserAccountEmailTakenException ex) {
        log.warn("email: {} is already taken.", ex.getResourceId(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }

    @ExceptionHandler(UserAccountVerificationNotPendingException.class) // Handle conflict (409)
    public ResponseEntity<ApiResponse<String>> handleAccountVerificationNotPending(
            UserAccountVerificationNotPendingException ex
    ) {
        log.warn("account: {} is not pending for verification.", ex.getResourceId());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex, HttpStatus.CONFLICT, messageSource));
    }


    @ExceptionHandler(UserAccountInvalidPasswordConfirmationException.class) // 403
    public ResponseEntity<ApiResponse<String>> handlAccountInvalidPasswordConfirmation(
            UserAccountInvalidPasswordConfirmationException ex
    ) {
        log.warn("user: {} failed to confirm password.", ex.getResourceId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(UserAccountUpdateException.class) // 400
    public ResponseEntity<ApiResponse<String>> handleUserAccountUpdate(
            UserAccountUpdateException ex
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