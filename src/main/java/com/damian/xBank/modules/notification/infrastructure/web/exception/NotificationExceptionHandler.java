package com.damian.xBank.modules.notification.infrastructure.web.exception;

import com.damian.xBank.modules.notification.domain.exception.NotificationException;
import com.damian.xBank.modules.notification.domain.exception.NotificationNotFoundException;
import com.damian.xBank.modules.notification.domain.exception.NotificationNotOwnerException;
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
public class NotificationExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(NotificationExceptionHandler.class);

    private final MessageSource messageSource;

    public NotificationExceptionHandler(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(NotificationNotOwnerException.class)
    public ResponseEntity<ApiResponse<String>> handleException(NotificationNotOwnerException ex) {
        log.warn("Notification: {} not owner", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ApiResponse.error(ex, HttpStatus.UNAUTHORIZED, messageSource));
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotificationNotFoundException ex) {
        log.warn("Notification: {} not found", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }


    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponse<String>> handleApplicationException(NotificationException ex) {
        log.warn("Notification: {} internal error.", ex.getResourceId());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex, HttpStatus.INTERNAL_SERVER_ERROR, messageSource));
    }
}