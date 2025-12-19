package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;
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
public class ImageExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ImageExceptionHandler.class);
    private final MessageSource messageSource;

    public ImageExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ImageEmptyFileException.class) // 400
    public ResponseEntity<ApiResponse<String>> handleBadRequest(ApplicationException ex) {
        log.warn("Image file is empty.", ex);

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ImageCompressionFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleCompressionFailed(ImageCompressionFailedException ex) {
        log.error("Image compression failed.", ex);

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(
                                     message,
                                     HttpStatus.INTERNAL_SERVER_ERROR
                             ));
    }

    @ExceptionHandler(ImageResizeFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleResizeFailed(ImageResizeFailedException ex) {
        log.error("Image resize failed.", ex);

        String message = messageSource.getMessage(
                ErrorCodes.STORAGE_IMAGE_UPLOAD_FAILED,
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(
                                     message,
                                     HttpStatus.INTERNAL_SERVER_ERROR
                             ));
    }

    @ExceptionHandler(ImageUploadFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleUploadFailed(ImageUploadFailedException ex) {
        log.error("Image upload failed at: {} failed.", ex.getResourceId(), ex);

        String message = messageSource.getMessage(
                ErrorCodes.STORAGE_IMAGE_UPLOAD_FAILED,
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(
                                     message,
                                     HttpStatus.INTERNAL_SERVER_ERROR
                             ));
    }

    @ExceptionHandler(ImageNotFoundException.class) // 404
    public ResponseEntity<ApiResponse<String>> handleNotFound(ImageNotFoundException ex) {
        log.warn("Image: {} not found in: {}", ex.getResourceId(), ex.getArgs()[0]);

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(message, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ImageTooLargeException.class) // 413 Payload Too Large
    public ResponseEntity<ApiResponse<String>> handleTooLarge(ImageTooLargeException ex) {
        log.warn("Image size: {} is too large.", ex.getArgs()[0]);

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                             .body(ApiResponse.error(message, HttpStatus.PAYLOAD_TOO_LARGE));
    }

    @ExceptionHandler(ImageTypeNotSupportedException.class) // 415
    public ResponseEntity<ApiResponse<String>> invalidType(ImageTypeNotSupportedException ex) {
        log.warn("Image type: {} not supported.", ex.getResourceId());

        String message = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                             .body(ApiResponse.error(
                                     message,
                                     HttpStatus.UNSUPPORTED_MEDIA_TYPE
                             ));
    }
}