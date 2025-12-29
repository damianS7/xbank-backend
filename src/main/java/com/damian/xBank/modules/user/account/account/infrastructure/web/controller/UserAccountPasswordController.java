package com.damian.xBank.modules.user.account.account.infrastructure.web.controller;

import com.damian.xBank.modules.user.account.account.application.dto.request.UserAccountPasswordResetRequest;
import com.damian.xBank.modules.user.account.account.application.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.xBank.modules.user.account.account.application.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.xBank.modules.user.account.account.application.service.UserAccountPasswordService;
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
public class UserAccountPasswordController {
    private final UserAccountTokenService userAccountTokenService;
    private final UserAccountPasswordService userAccountPasswordService;

    public UserAccountPasswordController(
            UserAccountTokenService userAccountTokenService,
            UserAccountPasswordService userAccountPasswordService
    ) {
        this.userAccountTokenService = userAccountTokenService;
        this.userAccountPasswordService = userAccountPasswordService;
    }

    // endpoint to modify current user password
    @PatchMapping("/accounts/password")
    public ResponseEntity<?> updatePassword(
            @Validated @RequestBody
            UserAccountPasswordUpdateRequest request
    ) {
        userAccountPasswordService.updatePassword(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    // endpoint to request for a reset password
    @PostMapping("/accounts/password/reset")
    public ResponseEntity<?> resetPasswordRequest(
            @Validated @RequestBody
            UserAccountPasswordResetRequest request
    ) {
        // generate a new password reset token
        UserAccountToken userAccountToken = userAccountTokenService.generatePasswordResetToken(request);

        // send the email with the link to reset the password
        userAccountPasswordService.sendResetPasswordEmail(
                userAccountToken.getAccount().getEmail(),
                userAccountToken.getToken()
        );

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
            @Validated @RequestBody
            UserAccountPasswordResetSetRequest request
    ) {
        // update the password using the token
        userAccountPasswordService.passwordResetWithToken(token, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Password reset successfully."));
    }
}