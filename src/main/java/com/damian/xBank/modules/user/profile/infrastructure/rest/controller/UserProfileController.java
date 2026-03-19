package com.damian.xBank.modules.user.profile.infrastructure.rest.controller;

import com.damian.xBank.modules.user.profile.application.usecase.get.GetCurrentUserProfile;
import com.damian.xBank.modules.user.profile.application.usecase.get.GetUserProfileQuery;
import com.damian.xBank.modules.user.profile.application.usecase.get.GetUserProfileResult;
import com.damian.xBank.modules.user.profile.application.usecase.update.UpdateCurrentUserProfile;
import com.damian.xBank.modules.user.profile.application.usecase.update.UpdateUserProfileCommand;
import com.damian.xBank.modules.user.profile.application.usecase.update.UpdateUserProfileResult;
import com.damian.xBank.modules.user.profile.infrastructure.mapper.UserProfileDtoMapper;
import com.damian.xBank.modules.user.profile.infrastructure.rest.request.UserProfileUpdateRequest;
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

    /**
     * Endpoint para obtener el perfil del usuario actual.
     *
     * @return GetUserProfileResult
     */
    @GetMapping("/profiles")
    public ResponseEntity<?> getLoggedCustomerData() {
        GetUserProfileQuery query = new GetUserProfileQuery();
        GetUserProfileResult result = getCurrentUserProfile.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    /**
     * Endpoint para modificar el perfil de usuario.
     *
     * @param request UserProfileUpdateRequest
     * @return UpdateUserProfileResult
     */
    @PatchMapping("/profiles")
    public ResponseEntity<?> update(
        @Valid @RequestBody
        UserProfileUpdateRequest request
    ) {
        UpdateUserProfileCommand command = UserProfileDtoMapper.toCommand(request);
        UpdateUserProfileResult result = updateCurrentUserProfile.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }
}

