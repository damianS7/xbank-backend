package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Order(1)
@RestControllerAdvice
public class FileStorageExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(FileStorageExceptionHandler.class);
    private final MessageSource messageSource;

    public FileStorageExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(FileStorageNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleFileNotFound(FileStorageNotFoundException ex) {
        log.error("File: {} not found in: {}", ex.getArgs()[1], ex.getArgs()[0]);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex, HttpStatus.NOT_FOUND, messageSource));
    }

    @ExceptionHandler(FileStorageFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleFileStorageFailed(FileStorageFailedException ex) {
        log.error("Failed to store file: {} at: {}", ex.getArgs()[0], ex.getArgs()[1]);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex, HttpStatus.INTERNAL_SERVER_ERROR, messageSource));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class) // 413 Payload Too Large
    public ResponseEntity<ApiResponse<String>> handleTooLarge(RuntimeException ex) {
        log.warn("File upload too large");

        ApplicationException exception = new ApplicationException(
                ErrorCodes.STORAGE_UPLOAD_FILE_TOO_LARGE,
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                             .body(ApiResponse.error(exception, HttpStatus.PAYLOAD_TOO_LARGE, messageSource));
    }
}