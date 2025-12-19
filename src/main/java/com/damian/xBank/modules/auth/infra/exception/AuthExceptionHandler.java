package com.damian.xBank.modules.auth.infra.exception;

import com.damian.xBank.modules.auth.domain.exception.EmailNotFoundException;
import com.damian.xBank.modules.auth.domain.exception.UserAccountNotVerifiedException;
import com.damian.xBank.modules.auth.domain.exception.UserAccountSuspendedException;
import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @ExceptionHandler(
            {
                    UsernameNotFoundException.class,
                    EmailNotFoundException.class,
                    BadCredentialsException.class
            }
    )
    public ResponseEntity<?> handleBadCredentials(RuntimeException e) {
        log.warn("Failed login attempt. Bad credentials.", e);

        String message = messageSource.getMessage(
                ErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS,
                null,
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ApiResponse.error(
                                     message,
                                     HttpStatus.UNAUTHORIZED
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

        String message = messageSource.getMessage(
                ErrorCodes.USER_ACCOUNT_SUSPENDED,
                null,
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(
                                     message, HttpStatus.FORBIDDEN
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

        String message = messageSource.getMessage(
                ErrorCodes.USER_ACCOUNT_NOT_VERIFIED,
                null,
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(
                                     message, HttpStatus.FORBIDDEN
                             ));
    }
}