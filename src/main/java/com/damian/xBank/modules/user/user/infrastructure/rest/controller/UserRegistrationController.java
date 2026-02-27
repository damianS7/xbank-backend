package com.damian.xBank.modules.user.user.infrastructure.rest.controller;

import com.damian.xBank.modules.user.user.application.cqrs.command.UserRegistrationCommand;
import com.damian.xBank.modules.user.user.application.cqrs.result.UserRegistrationResult;
import com.damian.xBank.modules.user.user.application.usecase.UserRegister;
import com.damian.xBank.modules.user.user.infrastructure.mapper.UserDtoMapper;
import com.damian.xBank.modules.user.user.infrastructure.rest.dto.request.UserRegistrationRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class UserRegistrationController {
    private final UserRegister userRegister;

    @Autowired
    public UserRegistrationController(
        UserRegister userRegister
    ) {
        this.userRegister = userRegister;
    }

    // endpoint for the current user to upload his profile photo
    @PostMapping("/users/register")
    public ResponseEntity<?> registerCustomer(
        @Valid @RequestBody
        UserRegistrationRequest request
    ) {

        UserRegistrationCommand command = UserDtoMapper.toCommand(request);
        UserRegistrationResult userRegistrationResult = userRegister.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userRegistrationResult);
    }
}

