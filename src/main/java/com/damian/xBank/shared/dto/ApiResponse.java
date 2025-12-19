package com.damian.xBank.shared.dto;

import com.damian.xBank.shared.exception.ApplicationException;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message;
    private Object resourceId;
    private T data;
    private String errorCode;
    private Map<String, String> errors;

    public ApiResponse() {
    }

    public ApiResponse(String message, T data, int status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, null, HttpStatus.OK.value());
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(null, data, HttpStatus.OK.value());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, HttpStatus.OK.value());
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return new ApiResponse<>(message, null, status.value());
    }

    public static <T> ApiResponse<T> error(String error, Map<String, String> errors, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(error, null, status.value());
        response.setErrors(errors);
        return response;
    }

    public static <T> ApiResponse<T> error(
            ApplicationException exception,
            HttpStatus status,
            MessageSource messageSource
    ) {
        ApiResponse<T> response = error(exception.getErrorCode(), exception.getArgs(), status, messageSource);
        response.setResourceId(exception.getResourceId());
        return response;
    }

    public static <T> ApiResponse<T> error(
            String errorCode,
            Object[] args,
            HttpStatus status,
            MessageSource messageSource
    ) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(status.value());
        response.setErrorCode(errorCode);
        response.setMessage(
                messageSource.getMessage(
                        errorCode,
                        args,
                        LocaleContextHolder.getLocale()
                )
        );

        return response;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public Object getResourceId() {
        return resourceId;
    }

    public void setResourceId(Object resourceId) {
        this.resourceId = resourceId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}