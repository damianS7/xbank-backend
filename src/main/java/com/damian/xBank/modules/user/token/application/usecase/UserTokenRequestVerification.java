package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.dto.request.UserTokenVerificationRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * It sends an email with a verification link (token)
 */
@Service
public class UserTokenRequestVerification {
    private static final Logger log = LoggerFactory.getLogger(UserTokenRequestVerification.class);
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final UserTokenLinkBuilder userTokenLinkBuilder;
    private final UserTokenVerificationNotifier userTokenVerificationNotifier;

    public UserTokenRequestVerification(
            UserTokenRepository userTokenRepository,
            UserRepository userRepository,
            UserTokenLinkBuilder userTokenLinkBuilder,
            UserTokenVerificationNotifier userTokenVerificationNotifier
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
        this.userTokenLinkBuilder = userTokenLinkBuilder;
        this.userTokenVerificationNotifier = userTokenVerificationNotifier;
    }

    /**
     * Create a new verification token associated to the email and sent it with a verification link
     *
     * @param request The email address of the user.
     * @throws UserNotFoundException               If the user is not found.
     * @throws UserVerificationNotPendingException If the account is not pending for verification.
     */
    public void execute(UserTokenVerificationRequest request) {
        log.debug("Generating verification token for: {}", request.email());

        // retrieve the user by email
        User user = userRepository.findByEmail(request.email()).orElseThrow(
                () -> {
                    log.error("Failed to generate verification token. UserAccount for: {} not found.", request.email());
                    return new UserNotFoundException(request.email());
                }
        );

        // only account pending for verification can request email
        user.assertAwaitingVerification();

        // check if AccountToken exists orElse create a new one
        UserToken userToken = userTokenRepository
                .findByUser_Id(user.getId())
                .orElseGet(() -> {
                    log.warn("No previous token found. A new one will be created.");
                    return new UserToken(user);
                });

        // we set the accountToken data
        userToken.generateVerificationToken();

        // save the token to the database
        userTokenRepository.save(userToken);

        // create the verification link
        String verificationLink = userTokenLinkBuilder.buildAccountVerificationLink(userToken.getToken());

        // Send email to the user with the verification link
        userTokenVerificationNotifier.sendVerificationToken(request.email(), verificationLink);

        log.debug("Verification token: {} generated and sent to: {}", userToken.getToken(), request.email());
    }
}
