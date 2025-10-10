package com.damian.whatsapp.modules.user.account.account.controller;

import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountEmailUpdateRequest;
import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountRegistrationRequest;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountRegistrationService;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountService;
import com.damian.whatsapp.modules.user.user.dto.mapper.UserDtoMapper;
import com.damian.whatsapp.modules.user.user.dto.response.UserDto;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccount;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserAccountController {
    private final UserAccountRegistrationService userAccountRegistrationService;
    private final UserAccountService userAccountService;

    public UserAccountController(
            UserAccountRegistrationService userAccountRegistrationService,
            UserAccountService userAccountService
    ) {
        this.userAccountRegistrationService = userAccountRegistrationService;
        this.userAccountService = userAccountService;
    }

    // endpoint to modify current user email
    @PatchMapping("/accounts/email")
    public ResponseEntity<UserDto> updateEmail(
            @Validated @RequestBody
            UserAccountEmailUpdateRequest request
    ) {
        UserAccount userAccount = userAccountService.updateEmail(request);
        UserDto userDto = UserDtoMapper.toUserDto(userAccount.getOwner());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    // endpoint for account registration
    @PostMapping("/accounts/register")
    public ResponseEntity<?> register(
            @Validated @RequestBody
            UserAccountRegistrationRequest request
    ) {
        User registeredUser = userAccountRegistrationService.registerAccount(request);

        UserDto dto = UserDtoMapper.toUserDto(registeredUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }
}