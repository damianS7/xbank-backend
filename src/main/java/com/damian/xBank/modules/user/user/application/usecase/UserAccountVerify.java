package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.token.domain.model.UserAccountToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserAccountVerificationService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountVerify {
    private static final Logger log = LoggerFactory.getLogger(UserAccountVerify.class);
    private final Environment env;
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailSenderService emailSenderService;
    private final UserAccountVerificationService userAccountVerificationService;
    private final UserAccountTokenService userAccountTokenService;

    public UserAccountVerify(
            Environment env,
            UserAccountTokenRepository userAccountTokenRepository,
            UserAccountRepository userAccountRepository,
            EmailSenderService emailSenderService,
            UserAccountVerificationService userAccountVerificationService,
            UserAccountTokenService userAccountTokenService
    ) {
        this.env = env;
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.emailSenderService = emailSenderService;
        this.userAccountVerificationService = userAccountVerificationService;
        this.userAccountTokenService = userAccountTokenService;
    }

    /**
     * Activate an account using the token
     *
     * @param token the token to activate the account.
     * @throws UserAccountNotFoundException               when the account cannot be found.
     * @throws UserAccountVerificationNotPendingException when the account is not pending for activation.
     */
    public User execute(String token) {
        // check the token is valid and not expired.
        UserAccountToken userAccountToken = userAccountTokenService.validateToken(token);

        log.debug("Verifying account from user: {}", userAccountToken.getAccount().getId());

        User account = userAccountToken.getAccount();

        // checks if the account is pending for activation.
        if (!account.getStatus().equals(UserStatus.PENDING_VERIFICATION)) {
            log.warn("Failed to verify account. UserAccount is not awaiting verification.");
            throw new UserAccountVerificationNotPendingException(account.getId());
        }

        // mark the token as used
        userAccountToken.setUsed(true);
        userAccountTokenRepository.save(userAccountToken);

        // update account status to active
        account.setStatus(UserStatus.VERIFIED);

        // set the time at what the account was updated
        account.setUpdatedAt(Instant.now());

        // send email to user after account has been verificated
        userAccountVerificationService.sendConfirmedVerificationEmail(account);

        log.debug("UserAccount successfully verified.");
        return userAccountRepository.save(account);
    }
}
