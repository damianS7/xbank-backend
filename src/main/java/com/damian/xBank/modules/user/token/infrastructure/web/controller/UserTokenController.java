package com.damian.xBank.modules.user.token.infrastructure.web.controller;

import com.damian.xBank.modules.user.token.application.dto.request.UserTokenRequestPasswordResetRequest;
import com.damian.xBank.modules.user.token.application.dto.request.UserTokenResetPasswordRequest;
import com.damian.xBank.modules.user.token.application.dto.request.UserTokenVerificationRequest;
import com.damian.xBank.modules.user.token.application.usecase.UserTokenRequestPasswordReset;
import com.damian.xBank.modules.user.token.application.usecase.UserTokenRequestVerification;
import com.damian.xBank.modules.user.token.application.usecase.UserTokenResetPassword;
import com.damian.xBank.modules.user.token.application.usecase.UserTokenVerifyAccount;
import com.damian.xBank.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1")
public class UserTokenController {
    private final UserTokenRequestPasswordReset userTokenRequestPasswordReset;
    private final UserTokenResetPassword userTokenResetPassword;
    private final UserTokenVerifyAccount userTokenVerifyAccount;
    private final UserTokenRequestVerification userTokenRequestVerification;

    public UserTokenController(
            UserTokenRequestPasswordReset userTokenRequestPasswordReset,
            UserTokenResetPassword userTokenResetPassword,
            UserTokenVerifyAccount userTokenVerifyAccount,
            UserTokenRequestVerification userTokenRequestVerification
    ) {
        this.userTokenRequestPasswordReset = userTokenRequestPasswordReset;
        this.userTokenResetPassword = userTokenResetPassword;
        this.userTokenVerifyAccount = userTokenVerifyAccount;
        this.userTokenRequestVerification = userTokenRequestVerification;
    }

    // endpoint for account verification
    @GetMapping("/accounts/verification/{token:.+}")
    public ResponseEntity<?> verifyAccount(
            @PathVariable @NotBlank
            String token
    ) {
        // verification the account using the provided token
        userTokenVerifyAccount.execute(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Your account has been verified. You can now log in with your credentials."));
    }

    // endpoint for account to request for account verification email
    @PostMapping("/accounts/verification/resend")
    public ResponseEntity<?> resendVerification(
            @Valid @RequestBody
            UserTokenVerificationRequest request
    ) {
        // send the account verification link
        userTokenRequestVerification.execute(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "A verification link has been sent to your email."));
    }

    // endpoint to request for a reset password
    @PostMapping("/accounts/password/reset")
    public ResponseEntity<?> resetPasswordRequest(
            @Valid @RequestBody
            UserTokenRequestPasswordResetRequest request
    ) {

        // send the email with the link to reset the password
        userTokenRequestPasswordReset.execute(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "A password reset link has been sent to your email address."));
    }

    // endpoint to set a new password using token
    @PostMapping("/accounts/password/reset/{token:.+}")
    public ResponseEntity<?> resetPassword(
            @PathVariable @NotBlank
            String token,
            @Valid @RequestBody
            UserTokenResetPasswordRequest request
    ) {
        // update the password using the token
        userTokenResetPassword.execute(token, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Password reset successfully."));
    }
}