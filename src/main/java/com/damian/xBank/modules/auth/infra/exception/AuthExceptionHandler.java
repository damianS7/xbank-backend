package com.damian.xBank.modules.auth.infra.exception;

import com.damian.xBank.modules.auth.domain.exception.EmailNotFoundException;
import com.damian.xBank.modules.auth.domain.exception.UserAccountNotVerifiedException;
import com.damian.xBank.modules.auth.domain.exception.UserAccountSuspendedException;
import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// TODO review this
@Order(1)
@RestControllerAdvice
public class AuthExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthExceptionHandler.class);
    private final MessageSource messageSource;

    public AuthExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    //    @ExceptionHandler(UsernameNotFoundException.class) // 404
    //    public ResponseEntity<ApiResponse<String>> handleNotFound(UsernameNotFoundException ex) {
    //        log.warn("Attempt to access non existing. settingId: {}", ex.getResourceId(), ex);
    //
    //        String message = messageSource.getMessage(
    //                ex.getErrorCode(),
    //                ex.getArgs(),
    //                LocaleContextHolder.getLocale()
    //        );
    //
    //        return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                             .body(ApiResponse.error(message, HttpStatus.NOT_FOUND));
    //    }

    @ExceptionHandler(
            {
                    UsernameNotFoundException.class,
                    EmailNotFoundException.class,
                    BadCredentialsException.class
            }
    )
    public ResponseEntity<?> handleBadCredentials(RuntimeException e) {
        log.warn("Failed login attempt. Bad credentials.", e);

        //        String message = messageSource.getMessage(
        //                ex.getErrorCode(),
        //                ex.getArgs(),
        //                LocaleContextHolder.getLocale()
        //        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ApiResponse.error(Exceptions.USER_ACCOUNT_BAD_CREDENTIALS));
    }

    @ExceptionHandler(
            {
                    LockedException.class,
                    UserAccountSuspendedException.class
            }
    )
    public ResponseEntity<?> handleLocked(RuntimeException e) {
        log.warn("Failed login attempt. Account is suspended.", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(Exceptions.USER_ACCOUNT_SUSPENDED));
    }

    @ExceptionHandler(
            {
                    UserAccountNotVerifiedException.class,
                    DisabledException.class
            }
    )
    public ResponseEntity<?> handleDisabled(RuntimeException e) {
        log.warn("Failed login attempt. Account not verified.", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(
                                     ApiResponse.error(Exceptions.USER_ACCOUNT_NOT_VERIFIED)
                             );
    }
}