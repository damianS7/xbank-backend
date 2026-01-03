package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.user.application.dto.mapper.UserAccountDtoMapper;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountEmailUpdateRequest;
import com.damian.xBank.modules.user.user.application.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.user.application.usecase.UserAccountEmailUpdate;
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
public class UserAccountController {
    private final UserAccountEmailUpdate userAccountEmailUpdate;

    public UserAccountController(
            UserAccountEmailUpdate userAccountEmailUpdate
    ) {
        this.userAccountEmailUpdate = userAccountEmailUpdate;
    }

    // endpoint to modify current user email
    @PatchMapping("/accounts/email")
    public ResponseEntity<UserAccountDto> updateEmail(
            @Validated @RequestBody
            UserAccountEmailUpdateRequest request
    ) {
        User user = userAccountEmailUpdate.execute(request);
        UserAccountDto userAccountDto = UserAccountDtoMapper.toUserDto(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAccountDto);
    }
}