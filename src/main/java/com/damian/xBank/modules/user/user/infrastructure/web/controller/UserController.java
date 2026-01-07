package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.user.application.dto.mapper.UserDtoMapper;
import com.damian.xBank.modules.user.user.application.dto.request.UserEmailUpdateRequest;
import com.damian.xBank.modules.user.user.application.dto.response.UserDetailDto;
import com.damian.xBank.modules.user.user.application.dto.response.UserDto;
import com.damian.xBank.modules.user.user.application.usecase.UserEmailUpdate;
import com.damian.xBank.modules.user.user.application.usecase.UserGet;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        User user = userGet.execute();
        UserDetailDto dto = UserDtoMapper.toUserDetailDto(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
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