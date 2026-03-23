package com.damian.xBank.modules.user.user.application.usecase.update;

import com.damian.xBank.modules.user.user.domain.exception.UserEmailTakenException;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para cambiar el email.
 */
@Service
public class UpdateCurrentUserEmail {
    private static final Logger log = LoggerFactory.getLogger(UpdateCurrentUserEmail.class);
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final UserRepository userRepository;

    public UpdateCurrentUserEmail(
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        UserRepository userRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.userRepository = userRepository;
    }

    /**
     * @param command
     * @throws UserNotFoundException
     * @throws UserEmailTakenException
     * @throws UserInvalidPasswordConfirmationException
     */
    @Transactional
    public void execute(UpdateUserEmailCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        User user = userRepository.findById(currentUser.getId()).orElseThrow(
            () -> new UserNotFoundException(currentUser.getId())
        );

        // Valida password introducida
        passwordValidator.validatePassword(currentUser, command.currentPassword());

        // Comprobar email si eiste
        if (userRepository.existsByEmail(command.newEmail())) {
            throw new UserEmailTakenException(command.newEmail());
        }

        log.debug("Updating user: {} to email: {}", user.getId(), command.newEmail());

        user.changeEmail(command.newEmail());
        userRepository.save(user);
    }
}
