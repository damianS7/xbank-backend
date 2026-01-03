package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordResetRequest;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.application.usecase.UserAccountPasswordUpdate;
import com.damian.xBank.modules.user.user.application.usecase.UserAccountPasswordUpdateWithToken;
import com.damian.xBank.modules.user.user.application.usecase.UserAccountRequestPasswordReset;
import com.damian.xBank.shared.dto.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserAccountPasswordController {
    private final UserAccountRequestPasswordReset userAccountRequestPasswordReset;
    private final UserAccountPasswordUpdateWithToken userAccountPasswordUpdateWithToken;
    private final UserAccountPasswordUpdate userAccountPasswordUpdate;

    public UserAccountPasswordController(
            UserAccountRequestPasswordReset userAccountRequestPasswordReset,
            UserAccountPasswordUpdateWithToken userAccountPasswordUpdateWithToken,
            UserAccountPasswordUpdate userAccountPasswordUpdate
    ) {
        this.userAccountRequestPasswordReset = userAccountRequestPasswordReset;
        this.userAccountPasswordUpdateWithToken = userAccountPasswordUpdateWithToken;
        this.userAccountPasswordUpdate = userAccountPasswordUpdate;
    }

    // endpoint to modify current user password
    @PatchMapping("/accounts/password")
    public ResponseEntity<?> updatePassword(
            @Validated @RequestBody
            UserAccountPasswordUpdateRequest request
    ) {
        userAccountPasswordUpdate.execute(request);

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

        // send the email with the link to reset the password
        userAccountRequestPasswordReset.execute(request);

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
        userAccountPasswordUpdateWithToken.execute(token, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Password reset successfully."));
    }
}