package com.damian.whatsapp.modules.user.account.account.controller;

import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountVerificationResendRequest;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountPasswordService;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountVerificationService;
import com.damian.whatsapp.shared.domain.UserAccount;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserAccountVerificationController {
    private final UserAccountPasswordService userAccountPasswordService;
    private final UserAccountVerificationService userAccountVerificationService;

    public UserAccountVerificationController(
            UserAccountPasswordService userAccountPasswordService,
            UserAccountVerificationService userAccountVerificationService
    ) {
        this.userAccountPasswordService = userAccountPasswordService;
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
                .body(com.damian.whatsapp.shared.util.ApiResponse.success(
                        "Your account has been verified. You can now log in with your credentials."));
    }

    // endpoint for account to request for account verification email
    @PostMapping("/accounts/verification/resend")
    public ResponseEntity<?> resendVerification(
            @Validated @RequestBody
            UserAccountVerificationResendRequest request
    ) {
        // generate a new verification token
        UserAccountToken userAccountToken = userAccountVerificationService.generateVerificationToken(request.email());

        // send the account verification link
        userAccountVerificationService.sendAccountVerificationLinkEmail(request.email(), userAccountToken.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(com.damian.whatsapp.shared.util.ApiResponse.success(
                        "A verification link has been sent to your email."));
    }
}