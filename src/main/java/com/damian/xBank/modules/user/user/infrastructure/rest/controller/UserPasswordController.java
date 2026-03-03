package com.damian.xBank.modules.user.user.infrastructure.rest.controller;

import com.damian.xBank.modules.user.user.application.cqrs.command.UpdateUserPasswordCommand;
import com.damian.xBank.modules.user.user.application.usecase.UpdateCurrentUserPassword;
import com.damian.xBank.modules.user.user.infrastructure.mapper.UserDtoMapper;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserPasswordUpdateRequest;
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

    private final UpdateCurrentUserPassword updateCurrentUserPassword;

    public UserPasswordController(
        UpdateCurrentUserPassword updateCurrentUserPassword
    ) {
        this.updateCurrentUserPassword = updateCurrentUserPassword;
    }

    // endpoint to modify current user password
    @PatchMapping("/accounts/password")
    public ResponseEntity<?> updatePassword(
        @Valid @RequestBody
        UserPasswordUpdateRequest request
    ) {
        UpdateUserPasswordCommand command = UserDtoMapper.toCommand(request);
        updateCurrentUserPassword.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}