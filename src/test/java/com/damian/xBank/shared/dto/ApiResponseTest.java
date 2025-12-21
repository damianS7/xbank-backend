package com.damian.xBank.shared.dto;

import com.damian.xBank.shared.exception.ApplicationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiResponseTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private ApplicationException applicationException;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @Test
    @DisplayName("Should create success response with only message")
    void shouldCreateSuccessResponseWithOnlyMessage() throws JsonProcessingException {
        // given
        String message = "Operation successful";

        // when
        ApiResponse<Void> response = ApiResponse.success(message);
        String json = objectMapper.writeValueAsString(response);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isNull();
        assertThat(json).contains("\"status\":200");
        assertThat(json).contains("\"message\":\"Operation successful\"");
        assertThat(json).doesNotContain("\"data\"");
        assertThat(json).doesNotContain("\"errors\"");
    }

    @Test
    @DisplayName("Should create success response with only data")
    void shouldCreateSuccessResponseWithOnlyData() throws JsonProcessingException {
        // given
        Map<String, String> data = Collections.singletonMap("id", "123");

        // when
        ApiResponse<Map<String, String>> response = ApiResponse.success(data);
        String json = objectMapper.writeValueAsString(response);
        System.out.println(json);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getMessage()).isNull();
        assertThat(json).contains("\"status\":200");
        assertThat(json).contains("\"data\":{\"id\":\"123\"}");
        assertThat(json).doesNotContain("\"message\"");
    }

    @Test
    @DisplayName("Should create success response with message and data")
    void shouldCreateSuccessResponseWithMessageAndData() throws JsonProcessingException {
        // given
        String message = "Created";
        String data = "some-data";

        // when
        ApiResponse<String> response = ApiResponse.success(message, data);
        String json = objectMapper.writeValueAsString(response);
        System.out.println(json);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isEqualTo(data);

        assertThat(json).contains("\"status\":200");
        assertThat(json).contains("\"message\":\"Created\"");
        assertThat(json).contains("\"data\":\"some-data\"");
    }

    @Test
    @DisplayName("Should create simple error response")
    void shouldCreateSimpleErrorResponse() throws JsonProcessingException {
        // given
        String message = "Not Found";
        HttpStatus status = HttpStatus.NOT_FOUND;

        // when
        ApiResponse<Void> response = ApiResponse.error(message, status);
        String json = objectMapper.writeValueAsString(response);
        System.out.println(json);

        // then
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isNull();
        assertThat(json).contains("\"status\":404");
        assertThat(json).contains("\"message\":\"Not Found\"");
    }

    @Test
    @DisplayName("Should create error response with validation errors map")
    void shouldCreateErrorResponseWithValidationErrors() throws JsonProcessingException {
        // given
        String message = "Validation Failed";
        Map<String, String> errors = Collections.singletonMap("email", "Invalid format");
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // when
        ApiResponse<Void> response = ApiResponse.error(message, errors, status);
        String json = objectMapper.writeValueAsString(response);
        System.out.println(json);

        // then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getErrors()).isEqualTo(errors);

        assertThat(json).contains("\"errors\":{\"email\":\"Invalid format\"}");
    }

    @Test
    @DisplayName("Should create error response from ApplicationException with MessageSource")
    void shouldCreateErrorResponseFromApplicationException() throws JsonProcessingException {
        // given
        String errorCode = "user.not.found";
        Object[] args = new Object[]{"101"};
        Long resourceId = 101L;
        String translatedMessage = "User with ID 101 not found";

        // when
        when(applicationException.getErrorCode()).thenReturn(errorCode);
        when(applicationException.getArgs()).thenReturn(args);
        when(applicationException.getResourceId()).thenReturn(resourceId);
        when(messageSource.getMessage(eq(errorCode), eq(args), any(Locale.class)))
                .thenReturn(translatedMessage);

        ApiResponse<Void> response = ApiResponse.error(applicationException, HttpStatus.NOT_FOUND, messageSource);
        String json = objectMapper.writeValueAsString(response);
        System.out.println(json);

        // then
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo(translatedMessage);
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getResourceId()).isEqualTo(resourceId);
        assertThat(json).contains("\"status\":404");
        assertThat(json).contains("\"message\":\"User with ID 101 not found\"");
        assertThat(json).contains("\"errorCode\":\"user.not.found\"");
        assertThat(json).contains("\"resourceId\":101");
        assertThat(json).doesNotContain("\"data\"");
    }
}