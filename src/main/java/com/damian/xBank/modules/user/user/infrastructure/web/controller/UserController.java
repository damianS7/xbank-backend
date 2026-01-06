package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.user.application.dto.mapper.UserDtoMapper;
import com.damian.xBank.modules.user.user.application.dto.request.UserEmailUpdateRequest;
import com.damian.xBank.modules.user.user.application.dto.response.UserDto;
import com.damian.xBank.modules.user.user.application.usecase.UserEmailUpdate;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserEmailUpdate userEmailUpdate;

    public UserController(
            UserEmailUpdate userEmailUpdate
    ) {
        this.userEmailUpdate = userEmailUpdate;
    }

    // endpoint to modify current user email
    @PatchMapping("/accounts/email")
    public ResponseEntity<UserDto> updateEmail(
            @Validated @RequestBody
            UserEmailUpdateRequest request
    ) {
        User user = userEmailUpdate.execute(request);
        UserDto userDto = UserDtoMapper.toUserDto(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }
}