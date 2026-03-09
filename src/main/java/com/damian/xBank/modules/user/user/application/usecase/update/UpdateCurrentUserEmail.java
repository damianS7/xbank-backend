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

import java.time.Instant;

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
     * It updates the email from the logged user
     *
     * @param command that contains the current password and the new email.
     * @throws UserNotFoundException                    if the user does not exist
     * @throws UserEmailTakenException                  if the email is already taken
     * @throws UserInvalidPasswordConfirmationException if the password does not match
     */
    @Transactional
    public void execute(UpdateUserEmailCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // we get the User entity so we can save at the end
        User user = userRepository.findById(currentUser.getId()).orElseThrow(
            () -> new UserNotFoundException(currentUser.getId())
        );

        // Before making any changes we check that the password sent by the user matches the one in the entity
        passwordValidator.validatePassword(currentUser, command.currentPassword());

        // check if the email is already taken
        if (userRepository.existsByEmail(command.newEmail())) {
            throw new UserEmailTakenException(command.newEmail());
        }

        log.debug("Updating user: {} to email: {}", user.getId(), command.newEmail());

        // set the new email
        user.setEmail(command.newEmail());

        // we change the updateAt timestamp field
        user.setUpdatedAt(Instant.now());

        // save the changes
        userRepository.save(user);
    }
}
