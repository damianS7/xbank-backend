package com.damian.xBank.modules.notification.infra.exception;

import com.damian.xBank.modules.notification.domain.exception.NotificationException;
import com.damian.xBank.modules.notification.domain.exception.NotificationNotFoundException;
import com.damian.xBank.shared.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class NotificationExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(NotificationExceptionHandler.class);

    private final MessageSource messageSource;

    public NotificationExceptionHandler(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotificationNotFoundException ex) {
        log.warn("Notification: {} not found", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(message, HttpStatus.NOT_FOUND));
    }


    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponse<String>> handleApplicationException(NotificationException ex) {
        log.warn("Notification: {} internal error.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(message, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}