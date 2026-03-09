package com.damian.xBank.modules.user.user.application.usecase.update;

import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.service.UserPasswordService;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateCurrentUserPassword {
    private static final Logger log = LoggerFactory.getLogger(UpdateCurrentUserPassword.class);
    private final UserPasswordService userPasswordService;
    private final PasswordValidator passwordValidator;
    private final AuthenticationContext authenticationContext;

    public UpdateCurrentUserPassword(
        UserPasswordService userPasswordService,
        PasswordValidator passwordValidator,
        AuthenticationContext authenticationContext
    ) {
        this.userPasswordService = userPasswordService;
        this.passwordValidator = passwordValidator;
        this.authenticationContext = authenticationContext;
    }

    /**
     * It updates the password of the current user
     *
     * @param command the request body that contains the current password and the new password
     * @throws UserNotFoundException                    if the user does not exist
     * @throws UserInvalidPasswordConfirmationException if the password does not match
     */
    public void execute(UpdateUserPasswordCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Before making any changes we check that the password sent by the user matches the one in the entity
        passwordValidator.validatePassword(currentUser, command.currentPassword());

        // update the password
        userPasswordService.updatePassword(currentUser.getId(), command.newPassword());
    }
}
