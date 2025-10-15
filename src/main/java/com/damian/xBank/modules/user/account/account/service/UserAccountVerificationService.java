package com.damian.xBank.modules.user.account.account.service;

import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import com.damian.xBank.modules.user.account.account.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.exception.UserAccountVerificationNotPendingException;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.token.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.UserAccountTokenType;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenExpiredException;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenNotFoundException;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenUsedException;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.domain.UserAccountToken;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserAccountVerificationService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountVerificationService.class);
    private final Environment env;
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailSenderService emailSenderService;

    public UserAccountVerificationService(
            Environment env,
            UserAccountTokenRepository userAccountTokenRepository,
            UserAccountRepository userAccountRepository,
            EmailSenderService emailSenderService
    ) {
        this.env = env;
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.emailSenderService = emailSenderService;
    }

    /**
     * Activate an account using the token
     *
     * @param token the token to activate the account.
     * @throws UserAccountNotFoundException               when the account cannot be found.
     * @throws UserAccountVerificationNotPendingException when the account is not pending for activation.
     */
    public UserAccount verifyAccount(String token) {
        // check the token is valid and not expired.
        UserAccountToken userAccountToken = this.validateToken(token);

        log.debug("Verifying account from user: {}", userAccountToken.getAccount().getId());

        UserAccount account = userAccountToken.getAccount();

        // checks if the account is pending for activation.
        if (!account.getAccountStatus().equals(UserAccountStatus.PENDING_VERIFICATION)) {
            log.warn("Failed to verify account. UserAccount is not awaiting verification.");
            throw new UserAccountVerificationNotPendingException(
                    Exceptions.USER.ACCOUNT.VERIFICATION.NOT_ELIGIBLE,
                    account.getId()
            );
        }

        // mark the token as used
        userAccountToken.setUsed(true);
        userAccountTokenRepository.save(userAccountToken);

        // update account status to active
        account.setAccountStatus(
                UserAccountStatus.VERIFIED
        );

        // set the time at what the account was updated
        account.setUpdatedAt(Instant.now());

        log.debug("UserAccount successfully verified.");
        return userAccountRepository.save(account);
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
     * It sends a welcome message to the user email address after verification.
     *
     * @param user The user to send a welcome message to.
     */
    public void sendAccountVerifiedEmail(UserAccount user) {
        emailSenderService.send(
                user.getEmail(),
                "Welcome to Photogram!",
                "Your account has been verified successfully."
        );
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
     * It sends an email with a verification link
     *
     * @param email The email address to send the verification link to.
     * @param token The token that will be used to verify the user's account.
     */
    public void sendAccountVerificationLinkEmail(String email, String token) {
        String host = env.getProperty("app.frontend.host");
        String port = env.getProperty("app.frontend.port");
        String url = String.format("http://%s:%s", host, port);
        String activationLink = url + "/accounts/verification/" + token;

        // Send email to confirm registration
        emailSenderService.send(
                email,
                "Photogram account verification link.",
                "Please click on the link below to confirm your registration: \n\n" + activationLink
        );
    }
}
