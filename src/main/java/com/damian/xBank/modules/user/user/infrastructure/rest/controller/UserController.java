package com.damian.xBank.modules.user.user.infrastructure.rest.controller;

import com.damian.xBank.modules.user.user.application.usecase.get.GetCurrentUser;
import com.damian.xBank.modules.user.user.application.usecase.get.GetCurrentUserQuery;
import com.damian.xBank.modules.user.user.application.usecase.get.GetCurrentUserResult;
import com.damian.xBank.modules.user.user.application.usecase.update.UpdateCurrentUserEmail;
import com.damian.xBank.modules.user.user.application.usecase.update.UpdateUserEmailCommand;
import com.damian.xBank.modules.user.user.infrastructure.mapper.UserDtoMapper;
import com.damian.xBank.modules.user.user.infrastructure.rest.request.UserEmailUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final GetCurrentUser getCurrentUser;
    private final UpdateCurrentUserEmail updateCurrentUserEmail;

    public UserController(
        GetCurrentUser getCurrentUser,
        UpdateCurrentUserEmail updateCurrentUserEmail
    ) {
        this.getCurrentUser = getCurrentUser;
        this.updateCurrentUserEmail = updateCurrentUserEmail;
    }

    /**
     * Endpoint para obtener toda la info de un usuario
     *
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity<?> getLoggedUserData() {
        GetCurrentUserQuery query = new GetCurrentUserQuery();
        GetCurrentUserResult userResult = getCurrentUser.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userResult);
    }

    /**
     * Endpoint para cambiar password.
     *
     * @param request
     * @return
     */
    @PatchMapping("/users/email")
    public ResponseEntity<Void> updateEmail(
        @Valid @RequestBody
        UserEmailUpdateRequest request
    ) {
        UpdateUserEmailCommand command = UserDtoMapper.toCommand(request);
        updateCurrentUserEmail.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}