package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.user.application.dto.request.UserEmailUpdateRequest;
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

import java.time.Instant;

@Service
public class UserEmailUpdate {
    private static final Logger log = LoggerFactory.getLogger(UserEmailUpdate.class);
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final UserRepository userRepository;

    public UserEmailUpdate(
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
     * @param request that contains the current password and the new email.
     * @return the user updated
     * @throws UserNotFoundException                    if the user does not exist
     * @throws UserEmailTakenException                  if the email is already taken
     * @throws UserInvalidPasswordConfirmationException if the password does not match
     */
    public User execute(UserEmailUpdateRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // we get the User entity so we can save at the end
        User user = userRepository.findById(currentUser.getId()).orElseThrow(
                () -> new UserNotFoundException(currentUser.getId())
        );

        // Before making any changes we check that the password sent by the user matches the one in the entity
        passwordValidator.validatePassword(currentUser, request.currentPassword());

        // check if the email is already taken
        if (userRepository.existsByEmail(request.newEmail())) {
            throw new UserEmailTakenException(request.newEmail());
        }

        log.debug("Updating user: {} to email: {}", user.getId(), request.newEmail());

        // set the new email
        user.setEmail(request.newEmail());

        // we change the updateAt timestamp field
        user.setUpdatedAt(Instant.now());

        // save the changes
        return userRepository.save(user);
    }
}
