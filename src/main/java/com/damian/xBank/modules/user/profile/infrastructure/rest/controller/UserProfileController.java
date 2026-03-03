package com.damian.xBank.modules.user.profile.infrastructure.rest.controller;

import com.damian.xBank.modules.user.profile.application.cqrs.command.UpdateUserProfileCommand;
import com.damian.xBank.modules.user.profile.application.cqrs.query.GetUserProfileQuery;
import com.damian.xBank.modules.user.profile.application.cqrs.result.UserProfileResult;
import com.damian.xBank.modules.user.profile.application.usecase.GetCurrentUserProfile;
import com.damian.xBank.modules.user.profile.application.usecase.UpdateCurrentUserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.mapper.UserProfileDtoMapper;
import com.damian.xBank.modules.user.profile.infrastructure.rest.dto.request.UserProfileUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class UserProfileController {
    private final GetCurrentUserProfile getCurrentUserProfile;
    private final UpdateCurrentUserProfile updateCurrentUserProfile;

    @Autowired
    public UserProfileController(
        GetCurrentUserProfile getCurrentUserProfile,
        UpdateCurrentUserProfile updateCurrentUserProfile
    ) {
        this.getCurrentUserProfile = getCurrentUserProfile;
        this.updateCurrentUserProfile = updateCurrentUserProfile;
    }

    // endpoint to receive current customer
    @GetMapping("/profiles")
    public ResponseEntity<?> getLoggedCustomerData() {
        GetUserProfileQuery query = new GetUserProfileQuery();
        UserProfileResult userProfileResult = getCurrentUserProfile.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userProfileResult);
    }

    // endpoint to modify current customer profile
    @PatchMapping("/profiles")
    public ResponseEntity<UserProfileResult> update(
        @Valid @RequestBody
        UserProfileUpdateRequest request
    ) {
        UpdateUserProfileCommand command = UserProfileDtoMapper.toCommand(request);
        UserProfileResult userProfileResult = updateCurrentUserProfile.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userProfileResult);
    }
}

