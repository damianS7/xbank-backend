package com.damian.xBank.modules.user.account.token.service;

import com.damian.xBank.modules.user.account.account.dto.request.UserAccountPasswordResetRequest;
import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import com.damian.xBank.modules.user.account.account.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.exception.UserAccountVerificationNotPendingException;
import com.damian.xBank.modules.user.account.account.model.UserAccount;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.token.enums.UserAccountTokenType;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenExpiredException;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenNotFoundException;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenUsedException;
import com.damian.xBank.modules.user.account.token.model.UserAccountToken;
import com.damian.xBank.modules.user.account.token.repository.UserAccountTokenRepository;
import com.damian.xBank.shared.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserAccountTokenService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountTokenService.class);
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final UserAccountRepository userAccountRepository;

    public UserAccountTokenService(
            UserAccountTokenRepository userAccountTokenRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Validate if the token matches with the one in the database
     * also run checks for expiration and usage of the token.
     *
     * @param token the token to verify
     * @return AccountToken the token entity
     */
    public UserAccountToken validateToken(String token) {
        log.debug("Validating token");
        // check the token if it matches with the one in database
        UserAccountToken userAccountToken = userAccountTokenRepository
                .findByToken(token)
                .orElseThrow(
                        () -> {
                            log.error("Failed to validate token. Token not found.");
                            return new UserAccountTokenNotFoundException(
                                    Exceptions.USER.ACCOUNT.VERIFICATION.TOKEN.NOT_FOUND,
                                    token,
                                    null
                            );
                        }
                );

        // check expiration
        if (!userAccountToken.getExpiresAt().isAfter(Instant.now())) {
            log.error("Failed to validate token. Token expired.");
            throw new UserAccountTokenExpiredException(
                    Exceptions.USER.ACCOUNT.VERIFICATION.TOKEN.EXPIRED,
                    token,
                    userAccountToken.getAccount().getId()
            );
        }

        // check if token is already used
        if (userAccountToken.isUsed()) {
            log.error("Failed to validate token. Token used.");
            throw new UserAccountTokenUsedException(
                    Exceptions.USER.ACCOUNT.VERIFICATION.TOKEN.USED,
                    token,
                    userAccountToken.getAccount().getId()
            );
        }

        return userAccountToken;
    }

    /**
     * Create a new verification token associated to the user.
     *
     * @param email The email address of the user.
     * @return An AccountToken object containing the verification token.
     * @throws UserAccountNotFoundException               If the user is not found.
     * @throws UserAccountVerificationNotPendingException If the account is not pending for verification.
     */
    public UserAccountToken generateVerificationToken(String email) {
        log.debug("Generating verification token for: {}", email);
        // retrieve the user by email
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElseThrow(
                () -> {
                    log.error("Failed to generate verification token. UserAccount for: {} not found.", email);
                    return new UserAccountNotFoundException(Exceptions.USER.ACCOUNT.NOT_FOUND, email);
                }
        );

        // only account pending for verification can request the email
        if (!userAccount.getAccountStatus().equals(UserAccountStatus.PENDING_VERIFICATION)) {
            log.error(
                    "Failed to generate verification token. UserAccount for: {} is not awaiting verification.",
                    email
            );
            throw new UserAccountVerificationNotPendingException(
                    Exceptions.USER.ACCOUNT.VERIFICATION.NOT_ELIGIBLE,
                    email
            );
        }

        // check if AccountToken exists orElse create a new one
        UserAccountToken userAccountToken = userAccountTokenRepository
                .findByAccount_Id(userAccount.getId())
                .orElseGet(() -> {
                    log.debug("No previous token found. A new one will be created.");
                    return userAccountTokenRepository.save(new UserAccountToken(userAccount));
                });

        // we set the accountToken data
        userAccountToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION)
                        .setToken(UUID.randomUUID().toString())
                        .setCreatedAt(Instant.now())
                        .setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));

        log.debug("Verification token: {} for: {} generated.", userAccountToken.getToken(), email);
        // save the token to the database
        return userAccountTokenRepository.save(
                userAccountToken
        );
    }

    /**
     * Generate a token for password reset
     *
     * @param request the request containing the email of the user and password
     * @return AccountToken with the token
     */
    public UserAccountToken generatePasswordResetToken(UserAccountPasswordResetRequest request) {
        log.debug("Generating password reset token for email: {}", request.email());
        UserAccount userAccount = userAccountRepository
                .findByEmail(request.email())
                .orElseThrow(
                        () -> {
                            log.error(
                                    "Failed to generate password reset token. No account found for: {}",
                                    request.email()
                            );
                            return new UserAccountNotFoundException(Exceptions.USER.ACCOUNT.NOT_FOUND, request.email());
                        }
                );

        // generate the token for password reset
        UserAccountToken token = new UserAccountToken(userAccount);
        token.setType(UserAccountTokenType.RESET_PASSWORD);

        log.debug("Password reset token generated successfully for email: {}", request.email());
        return userAccountTokenRepository.save(token);
    }
}
