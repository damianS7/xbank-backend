package com.damian.xBank.modules.auth.infrastructure.web.controller;

import com.damian.xBank.modules.auth.application.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.application.dto.AuthenticationResponse;
import com.damian.xBank.modules.auth.application.usecase.AuthenticationLogin;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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