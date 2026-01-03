package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.user.application.dto.mapper.UserAccountDtoMapper;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountRegistrationRequest;
import com.damian.xBank.modules.user.user.application.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.user.application.usecase.UserAccountRegister;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class UserRegistrationController {
    private final UserAccountRegister userAccountRegister;

    @Autowired
    public UserRegistrationController(
            UserAccountRegister userAccountRegister
    ) {
        this.userAccountRegister = userAccountRegister;
    }

    // endpoint for the current user to upload his profile photo
    @PostMapping("/users/register")
    public ResponseEntity<?> registerCustomer(
            @Validated @RequestBody
            UserAccountRegistrationRequest request
    ) {

        User registeredUser = userAccountRegister.execute(request);
        UserAccountDto userDto = UserAccountDtoMapper.toUserDto(registeredUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDto);
    }
}

