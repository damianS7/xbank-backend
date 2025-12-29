package com.damian.xBank.modules.setting.infrastructure.web.exception;

import com.damian.xBank.modules.setting.domain.exception.SettingNotFoundException;
import com.damian.xBank.modules.setting.domain.exception.SettingNotOwnerException;
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
public class SettingExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(SettingExceptionHandler.class);
    private final MessageSource messageSource;

    public SettingExceptionHandler(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(SettingNotOwnerException.class) // 403
    public ResponseEntity<ApiResponse<String>> handleAuthorization(SettingNotOwnerException ex) {
        log.warn("Attempt to access someone else settings. userId: {}", ex.getResourceId(), ex);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex, HttpStatus.FORBIDDEN, messageSource));
    }

    @ExceptionHandler(SettingNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleNotFound(SettingNotFoundException ex) {
        log.warn("Attempt to access non existing. settingId: {}", ex.getResourceId(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }
}