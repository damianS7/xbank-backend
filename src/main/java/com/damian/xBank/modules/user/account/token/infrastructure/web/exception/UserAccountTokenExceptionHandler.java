package com.damian.xBank.modules.user.account.token.infrastructure.web.exception;

import com.damian.xBank.modules.user.account.token.domain.exception.UserAccountTokenExpiredException;
import com.damian.xBank.modules.user.account.token.domain.exception.UserAccountTokenNotFoundException;
import com.damian.xBank.modules.user.account.token.domain.exception.UserAccountTokenUsedException;
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
public class UserAccountTokenExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UserAccountTokenExceptionHandler.class);
    private final MessageSource messageSource;

    public UserAccountTokenExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Token Exceptions
    @ExceptionHandler(UserAccountTokenNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleAccountTokenNotFound(
            UserAccountTokenNotFoundException ex
    ) {
        log.warn(
                "user: {} account token: {} not found.",
                ex.getResourceId(),
                ex.getArgs()[0]
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(UserAccountTokenUsedException.class) // 403
    public ResponseEntity<ApiResponse<String>> handleAccountVerificationTokenUsed(
            UserAccountTokenUsedException ex
    ) {
        log.warn(
                "User: {} account token: {} is already used.",
                ex.getResourceId(),
                ex.getArgs()[0]
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(UserAccountTokenExpiredException.class) // 410
    public ResponseEntity<ApiResponse<String>> handleAccountVerificationTokenExpired(
            UserAccountTokenExpiredException ex
    ) {
        log.warn(
                "account: {} account token: {} is expired.",
                ex.getResourceId(),
                ex.getArgs()[0]
        );

        return ResponseEntity.status(HttpStatus.GONE)
                             .body(ApiResponse.error(ex, HttpStatus.GONE, messageSource));
    }
}