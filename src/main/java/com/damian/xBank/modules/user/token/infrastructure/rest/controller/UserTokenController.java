package com.damian.xBank.modules.user.token.infrastructure.rest.controller;

import com.damian.xBank.modules.user.token.application.usecase.verification.request.RequestAccountVerification;
import com.damian.xBank.modules.user.token.application.usecase.verification.request.RequestAccountVerificationCommand;
import com.damian.xBank.modules.user.token.application.usecase.password.reset.RequestPasswordReset;
import com.damian.xBank.modules.user.token.application.usecase.password.reset.RequestPasswordResetCommand;
import com.damian.xBank.modules.user.token.application.usecase.password.reset.ResetPassword;
import com.damian.xBank.modules.user.token.application.usecase.password.reset.ResetPasswordCommand;
import com.damian.xBank.modules.user.token.application.usecase.verification.verify.VerifyAccount;
import com.damian.xBank.modules.user.token.application.usecase.verification.verify.VerifyAccountCommand;
import com.damian.xBank.modules.user.token.infrastructure.rest.request.RequestAccountVerificationRequest;
import com.damian.xBank.modules.user.token.infrastructure.rest.request.RequestPasswordResetRequest;
import com.damian.xBank.modules.user.token.infrastructure.rest.request.ResetPasswordRequest;
import com.damian.xBank.shared.infrastructure.web.dto.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
public class UserTokenController {
    private final RequestPasswordReset requestPasswordReset;
    private final ResetPassword resetPassword;
    private final VerifyAccount verifyAccount;
    private final RequestAccountVerification requestAccountVerification;

    public UserTokenController(
        RequestPasswordReset requestPasswordReset,
        ResetPassword resetPassword,
        VerifyAccount verifyAccount,
        RequestAccountVerification requestAccountVerification
    ) {
        this.requestPasswordReset = requestPasswordReset;
        this.resetPassword = resetPassword;
        this.verifyAccount = verifyAccount;
        this.requestAccountVerification = requestAccountVerification;
    }

    // endpoint for account verification
    @GetMapping("/accounts/verification/{token:.+}")
    public ResponseEntity<?> verifyAccount(
        @PathVariable @NotBlank
        String token
    ) {
        VerifyAccountCommand command = new VerifyAccountCommand(token);

        // verification the account using the provided token
        verifyAccount.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(
                "Your account has been verified. You can now log in with your credentials."));
    }

    // endpoint for account to request for account verification email
    @PostMapping("/accounts/verification/resend")
    public ResponseEntity<?> resendVerification(
        @Valid @RequestBody
        RequestAccountVerificationRequest request
    ) {
        RequestAccountVerificationCommand command = new RequestAccountVerificationCommand(
            request.email()
        );

        // send the account verification link
        requestAccountVerification.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(
                "A verification link has been sent to your email."));
    }

    // endpoint to request for a reset password
    @PostMapping("/accounts/password/reset")
    public ResponseEntity<?> resetPasswordRequest(
        @Valid @RequestBody
        RequestPasswordResetRequest request
    ) {

        RequestPasswordResetCommand command = new RequestPasswordResetCommand(
            request.email()
        );

        // send the email with the link to reset the password
        requestPasswordReset.execute(command);

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
        ResetPasswordRequest request
    ) {
        ResetPasswordCommand command = new ResetPasswordCommand(
            token,
            request.password()
        );

        // update the password using the token
        resetPassword.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success("Password reset successfully."));
    }
}