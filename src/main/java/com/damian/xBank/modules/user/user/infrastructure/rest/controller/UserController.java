package com.damian.xBank.modules.user.user.infrastructure.rest.controller;

import com.damian.xBank.modules.user.user.application.cqrs.command.UserEmailUpdateCommand;
import com.damian.xBank.modules.user.user.application.cqrs.result.UserResult;
import com.damian.xBank.modules.user.user.application.usecase.UserEmailUpdate;
import com.damian.xBank.modules.user.user.application.usecase.UserGet;
import com.damian.xBank.modules.user.user.infrastructure.mapper.UserDtoMapper;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserEmailUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserGet userGet;
    private final UserEmailUpdate userEmailUpdate;

    public UserController(
        UserGet userGet,
        UserEmailUpdate userEmailUpdate
    ) {
        this.userGet = userGet;
        this.userEmailUpdate = userEmailUpdate;
    }

    // endpoint to receive current customer
    @GetMapping("/users")
    public ResponseEntity<?> getLoggedUserData() {
        UserResult userResult = userGet.execute();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userResult);
    }

    // endpoint to modify current user email
    @PatchMapping("/users/email")
    public ResponseEntity<Void> updateEmail(
        @Valid @RequestBody
        UserEmailUpdateRequest request
    ) {
        UserEmailUpdateCommand command = UserDtoMapper.toCommand(request);
        userEmailUpdate.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}