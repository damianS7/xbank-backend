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

/**
 * Caso de uso para actualizar el password del usuario actual.
 */
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
     * @param command
     * @throws UserNotFoundException
     * @throws UserInvalidPasswordConfirmationException
     */
    public void execute(UpdateUserPasswordCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Validar password
        passwordValidator.validatePassword(currentUser, command.currentPassword());

        // Cambiar password
        userPasswordService.updatePassword(currentUser.getId(), command.newPassword());
    }
}
