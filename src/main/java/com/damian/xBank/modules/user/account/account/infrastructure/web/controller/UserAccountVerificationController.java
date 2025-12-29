package com.damian.xBank.modules.user.account.account.infrastructure.web.controller;

import com.damian.xBank.modules.user.account.account.application.dto.request.UserAccountVerificationResendRequest;
import com.damian.xBank.modules.user.account.account.application.service.UserAccountVerificationService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.token.application.service.UserAccountTokenService;
import com.damian.xBank.modules.user.account.token.domain.entity.UserAccountToken;
import com.damian.xBank.shared.dto.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserAccountVerificationController {
    private final UserAccountTokenService userAccountTokenService;
    private final UserAccountVerificationService userAccountVerificationService;

    public UserAccountVerificationController(
            UserAccountTokenService userAccountTokenService,
            UserAccountVerificationService userAccountVerificationService
    ) {
        this.userAccountTokenService = userAccountTokenService;
        this.userAccountVerificationService = userAccountVerificationService;
    }

    // endpoint for account verification
    @GetMapping("/accounts/verification/{token:.+}")
    public ResponseEntity<?> verifyAccount(
            @PathVariable @NotBlank
            String token
    ) {
        // verification the account using the provided token
        UserAccount account = userAccountVerificationService.verifyAccount(token);

        // send email to user after account has been verificated
        userAccountVerificationService.sendAccountVerifiedEmail(account);

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
        // generate a new verification token
        UserAccountToken userAccountToken = userAccountTokenService.generateVerificationToken(request.email());

        // send the account verification link
        userAccountVerificationService.sendAccountVerificationLinkEmail(request.email(), userAccountToken.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "A verification link has been sent to your email."));
    }
}