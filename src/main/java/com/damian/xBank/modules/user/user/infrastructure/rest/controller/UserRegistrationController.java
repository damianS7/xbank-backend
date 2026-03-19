package com.damian.xBank.modules.user.user.infrastructure.rest.controller;

import com.damian.xBank.modules.user.user.application.usecase.register.RegisterUser;
import com.damian.xBank.modules.user.user.application.usecase.register.RegisterUserCommand;
import com.damian.xBank.modules.user.user.application.usecase.register.RegisterUserResult;
import com.damian.xBank.modules.user.user.infrastructure.mapper.UserDtoMapper;
import com.damian.xBank.modules.user.user.infrastructure.rest.request.RegisterUserRequest;
import jakarta.validation.Valid;
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
    private final RegisterUser registerUser;

    public UserRegistrationController(
        RegisterUser registerUser
    ) {
        this.registerUser = registerUser;
    }

    /**
     * Endpoint para registrar nuevos usuarios
     *
     * @param request
     * @return
     */
    @PostMapping("/users/register")
    public ResponseEntity<?> registerCustomer(
        @Valid @RequestBody
        RegisterUserRequest request
    ) {

        RegisterUserCommand command = UserDtoMapper.toCommand(request);
        RegisterUserResult registerUserResult = registerUser.execute(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(registerUserResult);
    }
}

