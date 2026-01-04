package com.damian.xBank.modules.user.token.infrastructure.web.exception;

import com.damian.xBank.modules.user.token.domain.exception.UserTokenExpiredException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenNotFoundException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenUsedException;
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
public class UserTokenExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UserTokenExceptionHandler.class);
    private final MessageSource messageSource;

    public UserTokenExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Token Exceptions
    @ExceptionHandler(UserTokenNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleTokenNotFound(
            UserTokenNotFoundException ex
    ) {
        log.warn(
                "user: {} token: {} not found.",
                ex.getResourceId(),
                ex.getArgs()[0]
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(UserTokenUsedException.class) // 403
    public ResponseEntity<ApiResponse<String>> handleVerificationTokenUsed(
            UserTokenUsedException ex
    ) {
        log.warn(
                "User: {} token: {} is already used.",
                ex.getResourceId(),
                ex.getArgs()[0]
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(UserTokenExpiredException.class) // 410
    public ResponseEntity<ApiResponse<String>> handleVerificationTokenExpired(
            UserTokenExpiredException ex
    ) {
        log.warn(
                "user: {} token: {} is expired.",
                ex.getResourceId(),
                ex.getArgs()[0]
        );

        return ResponseEntity.status(HttpStatus.GONE)
                             .body(ApiResponse.error(ex, HttpStatus.GONE, messageSource));
    }
}