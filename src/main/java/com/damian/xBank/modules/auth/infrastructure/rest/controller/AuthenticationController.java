package com.damian.xBank.modules.auth.infrastructure.rest.controller;

import com.damian.xBank.modules.auth.application.usecase.AuthenticationLogin;
import com.damian.xBank.modules.auth.infrastructure.rest.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.infrastructure.rest.dto.AuthenticationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("api/v1")
public class AuthenticationController {
    private final AuthenticationLogin authenticationLogin;

    public AuthenticationController(
        AuthenticationLogin authenticationLogin
    ) {
        this.authenticationLogin = authenticationLogin;
    }

    // endpoint for login
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
        @Valid @RequestBody
        AuthenticationRequest request
    ) {
        AuthenticationResponse authResponse = authenticationLogin.execute(request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.AUTHORIZATION, authResponse.token())
            .body(authResponse);
    }

    // endpoint for token validation
    @GetMapping("/auth/token/validate")
    public ResponseEntity<?> tokenValidation(
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }

    // endpoint for token validation
    @GetMapping("/test")
    public ResponseEntity<?> test(
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}