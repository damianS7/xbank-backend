package com.damian.xBank.modules.user.account.account.controller;

import com.damian.xBank.modules.user.account.account.dto.mapper.UserAccountDtoMapper;
import com.damian.xBank.modules.user.account.account.dto.request.UserAccountEmailUpdateRequest;
import com.damian.xBank.modules.user.account.account.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.account.account.service.UserAccountService;
import com.damian.xBank.shared.domain.UserAccount;
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
    private final UserAccountService userAccountService;

    public UserAccountController(
            UserAccountService userAccountService
    ) {
        this.userAccountService = userAccountService;
    }

    // endpoint to modify current user email
    @PatchMapping("/accounts/email")
    public ResponseEntity<UserAccountDto> updateEmail(
            @Validated @RequestBody
            UserAccountEmailUpdateRequest request
    ) {
        UserAccount userAccount = userAccountService.updateEmail(request);
        UserAccountDto userAccountDto = UserAccountDtoMapper.toUserDto(userAccount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAccountDto);
    }
}