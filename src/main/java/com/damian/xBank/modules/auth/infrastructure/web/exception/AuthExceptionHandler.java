package com.damian.xBank.modules.auth.infrastructure.web.exception;

import com.damian.xBank.modules.auth.domain.exception.UserAccountNotVerifiedException;
import com.damian.xBank.modules.auth.domain.exception.UserAccountSuspendedException;
import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class AuthExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthExceptionHandler.class);
    private final MessageSource messageSource;

    public AuthExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        log.warn("Failed login attempt. Bad credentials.", e);

        ApplicationException ex = new ApplicationException(
                ErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS,
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ApiResponse.error(
                                     ex,
                                     HttpStatus.UNAUTHORIZED,
                                     messageSource
                             ));
    }

    @ExceptionHandler(
            {
                    LockedException.class,
                    UserAccountSuspendedException.class
            }
    )
    public ResponseEntity<?> handleLocked(RuntimeException e) {
        log.warn("Failed login attempt. Account is suspended.", e);

        ApplicationException ex = new ApplicationException(
                ErrorCodes.USER_ACCOUNT_SUSPENDED,
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(
                                     ex, HttpStatus.FORBIDDEN, messageSource
                             ));
    }

    @ExceptionHandler(
            {
                    UserAccountNotVerifiedException.class,
                    DisabledException.class
            }
    )
    public ResponseEntity<?> handleDisabled(RuntimeException e) {
        log.warn("Failed login attempt. Account not verified.", e);

        ApplicationException ex = new ApplicationException(
                ErrorCodes.USER_ACCOUNT_NOT_VERIFIED,
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(
                                     ex, HttpStatus.FORBIDDEN, messageSource
                             ));
    }
}