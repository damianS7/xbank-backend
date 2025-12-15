package com.damian.xBank.modules.user.account.account.application.service;

import com.damian.xBank.modules.user.account.account.application.dto.request.UserAccountEmailUpdateRequest;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountEmailTakenException;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.infra.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.token.application.service.UserAccountTokenService;
import com.damian.xBank.modules.user.account.token.domain.entity.UserAccountToken;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);
    private final UserAccountRepository userAccountRepository;
    private final UserAccountVerificationService userAccountVerificationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserAccountTokenService userAccountTokenService;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public UserAccountService(
            UserAccountRepository userAccountRepository,
            UserAccountVerificationService userAccountVerificationService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserAccountTokenService userAccountTokenService,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountVerificationService = userAccountVerificationService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userAccountTokenService = userAccountTokenService;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
    }

    /**
     * Creates a new user
     *
     * @param email    email to be registered
     * @param password password
     * @return the user created
     * @throws UserAccountEmailTakenException if another user has the email
     */
    public UserAccount createUserAccount(String email, String password, UserAccountRole role) {
        log.debug("Creating user account with email: {}", email);

        // check if the email is already taken
        if (userAccountRepository.existsByEmail(email)) {
            throw new UserAccountEmailTakenException(
                    Exceptions.USER.ACCOUNT.EMAIL_TAKEN, email
            );
        }

        // we create the user and assign the data
        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setCreatedAt(Instant.now());
        UserAccount registeredUser = userAccountRepository.save(user);

        // Create a token for the account activation
        UserAccountToken userAccountToken = userAccountTokenService.generateVerificationToken(email);

        // send the account activation link
        userAccountVerificationService.sendAccountVerificationLinkEmail(email, userAccountToken.getToken());


        log.debug(
                "user: {} with email:{} registered",
                registeredUser.getId(),
                registeredUser.getEmail()
        );

        return registeredUser;
    }

    /**
     * Returns all the user
     *
     * @return a list of UserDTO
     * @throws UserAccountNotFoundException if the logged user is not ADMIN
     */
    public Page<UserAccount> getUsers(Pageable pageable) {
        // we return all the user
        return userAccountRepository.findAll(pageable);
    }

    /**
     * Returns a user
     *
     * @param userId the id of the user to be returned
     * @return the user
     * @throws UserAccountNotFoundException if the user does not exist or if the logged user is not ADMIN
     */
    public UserAccount getUser(Long userId) {
        log.debug("Getting user: {}", userId);
        // if the user does not exist we throw an exception
        return userAccountRepository.findById(userId).orElseThrow(
                () -> new UserAccountNotFoundException(
                        Exceptions.USER.ACCOUNT.NOT_FOUND, userId
                )
        );
    }

    // returns the logged user
    public UserAccount getUser() {
        User currentUser = authenticationContext.getCurrentUser();
        return this.getUser(currentUser.getId());
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
    public UserAccount updateEmail(Long userId, String newEmail) {
        log.debug("Updating user: {} email: {}", userId, newEmail);

        // we get the User entity so we can save at the end
        UserAccount user = userAccountRepository.findById(userId).orElseThrow(
                () -> new UserAccountNotFoundException(Exceptions.USER.ACCOUNT.NOT_FOUND, userId)
        );

        // check if the email is already taken
        if (userAccountRepository.existsByEmail(newEmail)) {
            throw new UserAccountEmailTakenException(Exceptions.USER.ACCOUNT.EMAIL_TAKEN, newEmail);
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
    public UserAccount updateEmail(UserAccountEmailUpdateRequest request) {
        // we extract the email from the User stored in the SecurityContext
        final User currentUser = authenticationContext.getCurrentUser();

        // Before making any changes we check that the password sent by the user matches the one in the entity
        passwordValidator.validatePassword(currentUser, request.currentPassword());

        return this.updateEmail(currentUser.getId(), request.newEmail());
    }
}
