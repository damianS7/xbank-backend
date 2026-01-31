package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.user.application.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.application.usecase.UserPasswordUpdate;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
public class UserPasswordController {

    private final UserPasswordUpdate userPasswordUpdate;

    public UserPasswordController(
            UserPasswordUpdate userPasswordUpdate
    ) {
        this.userPasswordUpdate = userPasswordUpdate;
    }

    // endpoint to modify current user password
    @PatchMapping("/accounts/password")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody
            UserPasswordUpdateRequest request
    ) {
        userPasswordUpdate.execute(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}