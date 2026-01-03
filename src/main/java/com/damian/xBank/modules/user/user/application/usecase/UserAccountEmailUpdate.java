package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.user.application.dto.request.UserAccountEmailUpdateRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountEmailTakenException;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountEmailUpdate {
    private static final Logger log = LoggerFactory.getLogger(UserAccountEmailUpdate.class);
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final UserAccountRepository userAccountRepository;

    public UserAccountEmailUpdate(
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator,
            UserAccountRepository userAccountRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * It updates the email of a user
     *
     * @param userId   the id of the user
     * @param newEmail the new email to set
     * @return the user updated
     * @throws UserAccountNotFoundException   if the user does not exist
     * @throws UserAccountEmailTakenException if the email is already taken
     */
    public User updateEmail(Long userId, String newEmail) {
        log.debug("Updating user: {} email: {}", userId, newEmail);

        // we get the User entity so we can save at the end
        User user = userAccountRepository.findById(userId).orElseThrow(
                () -> new UserAccountNotFoundException(userId)
        );

        // check if the email is already taken
        if (userAccountRepository.existsByEmail(newEmail)) {
            throw new UserAccountEmailTakenException(newEmail);
        }

        // set the new email
        user.setEmail(newEmail);

        // we change the updateAt timestamp field
        user.setUpdatedAt(Instant.now());

        // save the changes
        return userAccountRepository.save(user);
    }

    /**
     * It updates the email from the logged user
     *
     * @param request that contains the current password and the new email.
     * @return the user updated
     * @throws UserAccountInvalidPasswordConfirmationException if the password does not match
     */
    public User execute(UserAccountEmailUpdateRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Before making any changes we check that the password sent by the user matches the one in the entity
        passwordValidator.validatePassword(currentUser, request.currentPassword());

        return this.updateEmail(currentUser.getId(), request.newEmail());
    }
}
