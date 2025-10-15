package com.damian.xBank.modules.user.account.account.exception;

import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenExpiredException;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenNotFoundException;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenUsedException;
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
public class UserAccountExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UserAccountExceptionHandler.class);

    // UserAccount exceptions
    @ExceptionHandler(UserAccountNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleUserAccountNotFound(
            UserAccountNotFoundException ex
    ) {
        log.warn(
                "user account: {} not found.",
                ex.getAccountId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(UserAccountEmailTakenException.class) // Handle conflict (409)
    public ResponseEntity<ApiResponse<String>> handleEmailAlreadyTaken(UserAccountEmailTakenException ex) {
        log.warn("email: {} is already taken.", ex.getEmail(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(UserAccountVerificationNotPendingException.class) // Handle conflict (409)
    public ResponseEntity<ApiResponse<String>> handleAccountVerificationNotPending(
            UserAccountVerificationNotPendingException ex
    ) {
        log.warn("account: {} is not pending for verification.", ex.getAccountId());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }


    @ExceptionHandler(UserAccountInvalidPasswordConfirmationException.class) // 403
    public ResponseEntity<ApiResponse<String>> handlAccountInvalidPasswordConfirmation(
            UserAccountInvalidPasswordConfirmationException ex
    ) {
        log.warn("user: {} failed to confirm password.", ex.getAccountId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(UserAccountUpdateException.class) // 400
    public ResponseEntity<ApiResponse<String>> handleUserAccountUpdate(
            UserAccountUpdateException ex
    ) {
        log.warn(
                "User id: {} failed to update field: {} with value: {}.",
                ex.getAccountId(),
                ex.getKey(),
                ex.getValue()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    // Token Exceptions

    @ExceptionHandler(UserAccountTokenNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleAccountTokenNotFound(
            UserAccountTokenNotFoundException ex
    ) {
        log.warn(
                "user: {} account token: {} not found.",
                ex.getAccountId(),
                ex.getToken()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(UserAccountTokenUsedException.class) // 403
    public ResponseEntity<ApiResponse<String>> handleAccountVerificationTokenUsed(
            UserAccountTokenUsedException ex
    ) {
        log.warn(
                "User: {} account token: {} is already used.",
                ex.getAccountId(),
                ex.getToken()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(UserAccountTokenExpiredException.class) // 410
    public ResponseEntity<ApiResponse<String>> handleAccountVerificationTokenExpired(
            UserAccountTokenExpiredException ex
    ) {
        log.warn(
                "account: {} account token: {} is expired.",
                ex.getAccountId(),
                ex.getToken()
        );
        return ResponseEntity.status(HttpStatus.GONE)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.GONE));
    }
}