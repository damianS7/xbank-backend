package com.damian.whatsapp.modules.user.account.account.service;

import com.damian.whatsapp.modules.user.account.account.UserAccountRepository;
import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountEmailUpdateRequest;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountEmailTakenException;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccount;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;

    public UserAccountService(
            UserRepository userRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * It updates the email of a user
     *
     * @param userId   the id of the user
     * @param newEmail the new email to set
     * @return the user updated
     * @throws UserNotFoundException          if the user does not exist
     * @throws UserAccountEmailTakenException if the email is already taken
     */
    public UserAccount updateEmail(Long userId, String newEmail) {
        log.debug("Updating user: {} email: {}", userId, newEmail);

        // we get the User entity so we can save at the end
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(Exceptions.USER.NOT_FOUND, userId)
        );

        // check if the email is already taken
        if (userRepository.existsByUserAccount_Email(newEmail)) {
            throw new UserAccountEmailTakenException(Exceptions.USER.EMAIL_TAKEN, newEmail);
        }

        // set the new email
        user.getAccount().setEmail(newEmail);

        // we change the updateAt timestamp field
        user.getAccount().setUpdatedAt(Instant.now());

        // save the changes
        return userAccountRepository.save(user.getAccount());
    }

    /**
     * It updates the email from the logged user
     *
     * @param request that contains the current password and the new email.
     * @return the user updated
     * @throws UserAccountInvalidPasswordConfirmationException if the password does not match
     */
    public UserAccount updateEmail(UserAccountEmailUpdateRequest request) {
        // we extract the email from the User stored in the SecurityContext
        final User currentUser = AuthHelper.getLoggedUser();

        // Before making any changes we check that the password sent by the user matches the one in the entity
        AuthHelper.validatePassword(currentUser, request.currentPassword());

        return this.updateEmail(currentUser.getId(), request.newEmail());
    }
}
