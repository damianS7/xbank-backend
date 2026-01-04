package com.damian.xBank.modules.user.token.infrastructure.service;

import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetRequest;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenExpiredException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenNotFoundException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenUsedException;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.model.UserTokenType;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserTokenService {
    private static final Logger log = LoggerFactory.getLogger(UserTokenService.class);
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;

    public UserTokenService(
            UserTokenRepository userTokenRepository,
            UserRepository userRepository
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Validate if the token matches with the one in the database
     * also run checks for expiration and usage of the token.
     *
     * @param token the token to verify
     * @return AccountToken the token entity
     */
    public UserToken validateToken(String token) {
        log.debug("Validating token");
        // check the token if it matches with the one in database
        UserToken userToken = userTokenRepository
                .findByToken(token)
                .orElseThrow(
                        () -> {
                            log.error("Failed to validate token. Token {} not found.", token);
                            return new UserTokenNotFoundException();
                        }
                );

        // check expiration
        if (!userToken.getExpiresAt().isAfter(Instant.now())) {
            log.error("Failed to validate token. Token {} expired.", token);
            throw new UserTokenExpiredException(userToken.getUser().getId(), token);
        }

        // check if token is already used
        if (userToken.isUsed()) {
            log.error("Failed to validate token. Token {} used.", token);
            throw new UserTokenUsedException(userToken.getUser().getId(), token);
        }

        return userToken;
    }

    /**
     * Create a new verification token associated to the user.
     *
     * @param email The email address of the user.
     * @return An AccountToken object containing the verification token.
     * @throws UserNotFoundException               If the user is not found.
     * @throws UserVerificationNotPendingException If the account is not pending for verification.
     */
    public UserToken generateVerificationToken(String email) {
        log.debug("Generating verification token for: {}", email);
        // retrieve the user by email
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> {
                    log.error("Failed to generate verification token. UserAccount for: {} not found.", email);
                    return new UserNotFoundException(email);
                }
        );

        // only account pending for verification can request the email
        if (!user.getStatus().equals(UserStatus.PENDING_VERIFICATION)) {
            log.error(
                    "Failed to generate verification token. UserAccount for: {} is not awaiting verification.",
                    email
            );
            throw new UserVerificationNotPendingException(email);
        }

        // check if AccountToken exists orElse create a new one
        UserToken userToken = userTokenRepository
                .findByUser_Id(user.getId())
                .orElseGet(() -> {
                    log.debug("No previous token found. A new one will be created.");
                    return userTokenRepository.save(new UserToken(user));
                });

        // we set the accountToken data
        userToken.setType(UserTokenType.ACCOUNT_VERIFICATION)
                 .setToken(UUID.randomUUID().toString())
                 .setCreatedAt(Instant.now())
                 .setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));

        log.debug("Verification token: {} for: {} generated.", userToken.getToken(), email);
        // save the token to the database
        return userTokenRepository.save(
                userToken
        );
    }

    /**
     * Generate a token for password reset
     *
     * @param request the request containing the email of the user and password
     * @return AccountToken with the token
     */
    public UserToken generatePasswordResetToken(UserPasswordResetRequest request) {
        log.debug("Generating password reset token for email: {}", request.email());
        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(
                        () -> {
                            log.error(
                                    "Failed to generate password reset token. No account found for: {}",
                                    request.email()
                            );
                            return new UserNotFoundException(request.email());
                        }
                );

        // generate the token for password reset
        UserToken token = new UserToken(user);
        token.setType(UserTokenType.RESET_PASSWORD);

        log.debug("Password reset token generated successfully for email: {}", request.email());
        return userTokenRepository.save(token);
    }
}
