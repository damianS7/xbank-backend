package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetRequest;
import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetSetRequest;
import com.damian.xBank.modules.user.token.application.usecase.UserTokenPasswordUpdate;
import com.damian.xBank.modules.user.token.application.usecase.UserTokenRequestPasswordReset;
import com.damian.xBank.modules.user.user.application.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.application.usecase.UserPasswordUpdate;
import com.damian.xBank.shared.dto.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserPasswordController {
    private final UserTokenRequestPasswordReset userTokenRequestPasswordReset;
    private final UserTokenPasswordUpdate userTokenPasswordUpdate;
    private final UserPasswordUpdate userPasswordUpdate;

    public UserPasswordController(
            UserTokenRequestPasswordReset userTokenRequestPasswordReset,
            UserTokenPasswordUpdate userTokenPasswordUpdate,
            UserPasswordUpdate userPasswordUpdate
    ) {
        this.userTokenRequestPasswordReset = userTokenRequestPasswordReset;
        this.userTokenPasswordUpdate = userTokenPasswordUpdate;
        this.userPasswordUpdate = userPasswordUpdate;
    }

    // endpoint to modify current user password
    @PatchMapping("/accounts/password")
    public ResponseEntity<?> updatePassword(
            @Validated @RequestBody
            UserPasswordUpdateRequest request
    ) {
        userPasswordUpdate.execute(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    // endpoint to request for a reset password
    @PostMapping("/accounts/password/reset")
    public ResponseEntity<?> resetPasswordRequest(
            @Validated @RequestBody
            UserPasswordResetRequest request
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
            @Validated @RequestBody
            UserPasswordResetSetRequest request
    ) {
        // update the password using the token
        userTokenPasswordUpdate.execute(token, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Password reset successfully."));
    }
}