package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.token.application.dto.request.UserAccountVerificationResendRequest;
import com.damian.xBank.modules.user.token.application.usecase.UserTokenResendVerification;
import com.damian.xBank.modules.user.token.application.usecase.UserTokenVerify;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.dto.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserVerificationController {
    private final UserTokenVerify userTokenVerify;
    private final UserTokenResendVerification userTokenResendVerification;

    public UserVerificationController(
            UserTokenVerify userTokenVerify,
            UserTokenResendVerification userTokenResendVerification
    ) {
        this.userTokenVerify = userTokenVerify;
        this.userTokenResendVerification = userTokenResendVerification;
    }

    // endpoint for account verification
    @GetMapping("/accounts/verification/{token:.+}")
    public ResponseEntity<?> verifyAccount(
            @PathVariable @NotBlank
            String token
    ) {
        // verification the account using the provided token
        User account = userTokenVerify.execute(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Your account has been verified. You can now log in with your credentials."));
    }

    // endpoint for account to request for account verification email
    @PostMapping("/accounts/verification/resend")
    public ResponseEntity<?> resendVerification(
            @Validated @RequestBody
            UserAccountVerificationResendRequest request
    ) {
        // send the account verification link
        userTokenResendVerification.execute(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "A verification link has been sent to your email."));
    }
}