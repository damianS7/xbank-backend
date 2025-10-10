package com.damian.whatsapp.modules.user.user.exception;

import com.damian.whatsapp.shared.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class UserExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UserExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
        log.warn("user: {} not found.", ex.getUserId(), ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(UserImageNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleUserImageNotFound(UserImageNotFoundException ex) {
        log.warn("Failed to find user: {} image.", ex.getUserId(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(UserUpdateException.class) // 400
    public ResponseEntity<ApiResponse<String>> handleUserUpdate(UserUpdateException ex) {
        log.warn("Failed to update user: {} ", ex.getUserId(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }
}