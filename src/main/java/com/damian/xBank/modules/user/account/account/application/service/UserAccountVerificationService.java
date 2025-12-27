package com.damian.xBank.modules.user.account.account.application.service;

import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountVerificationNotPendingException;
import com.damian.xBank.modules.user.account.account.infra.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.token.application.service.UserAccountTokenService;
import com.damian.xBank.modules.user.account.token.domain.entity.UserAccountToken;
import com.damian.xBank.modules.user.account.token.infra.repository.UserAccountTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountVerificationService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountVerificationService.class);
    private final Environment env;
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailSenderService emailSenderService;
    private final UserAccountTokenService userAccountTokenService;

    public UserAccountVerificationService(
            Environment env,
            UserAccountTokenRepository userAccountTokenRepository,
            UserAccountRepository userAccountRepository,
            EmailSenderService emailSenderService,
            UserAccountTokenService userAccountTokenService
    ) {
        this.env = env;
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.emailSenderService = emailSenderService;
        this.userAccountTokenService = userAccountTokenService;
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
        UserAccountToken userAccountToken = userAccountTokenService.validateToken(token);

        log.debug("Verifying account from user: {}", userAccountToken.getAccount().getId());

        UserAccount account = userAccountToken.getAccount();

        // checks if the account is pending for activation.
        if (!account.getAccountStatus().equals(UserAccountStatus.PENDING_VERIFICATION)) {
            log.warn("Failed to verify account. UserAccount is not awaiting verification.");
            throw new UserAccountVerificationNotPendingException(account.getId());
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
     * It sends an email with a verification link
     *
     * @param email The email address to send the verification link to.
     * @param token The token that will be used to verify the user's account.
     */
    public void sendAccountVerificationLinkEmail(String email, String token) {
        String host = env.getProperty("app.frontend.host");
        String port = env.getProperty("app.frontend.port");
        String url = String.format("http://%s:%s", host, port);
        String activationLink = url + "/customers/accounts/verification/" + token;

        // Send email to confirm registration
        emailSenderService.send(
                email,
                "xBank account verification link.",
                "Please click on the link below to confirm your registration: \n\n" + activationLink
        );
    }
}
