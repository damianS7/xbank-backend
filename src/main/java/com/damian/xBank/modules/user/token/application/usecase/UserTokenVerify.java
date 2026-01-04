package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserVerificationService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserTokenVerify {
    private static final Logger log = LoggerFactory.getLogger(UserTokenVerify.class);
    private final Environment env;
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final UserVerificationService userVerificationService;
    private final UserTokenService userTokenService;

    public UserTokenVerify(
            Environment env,
            UserTokenRepository userTokenRepository,
            UserRepository userRepository,
            EmailSenderService emailSenderService,
            UserVerificationService userVerificationService,
            UserTokenService userTokenService
    ) {
        this.env = env;
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.userVerificationService = userVerificationService;
        this.userTokenService = userTokenService;
    }

    /**
     * Activate an account using the token
     *
     * @param token the token to activate the account.
     * @throws UserNotFoundException               when the account cannot be found.
     * @throws UserVerificationNotPendingException when the account is not pending for activation.
     */
    public User execute(String token) {
        // check the token is valid and not expired.
        UserToken userToken = userTokenService.validateToken(token);

        log.debug("Verifying account from user: {}", userToken.getUser().getId());

        User account = userToken.getUser();

        // checks if the account is pending for activation.
        if (!account.getStatus().equals(UserStatus.PENDING_VERIFICATION)) {
            log.warn("Failed to verify account. UserAccount is not awaiting verification.");
            throw new UserVerificationNotPendingException(account.getId());
        }

        // mark the token as used
        userToken.setUsed(true);
        userTokenRepository.save(userToken);

        // update account status to active
        account.setStatus(UserStatus.VERIFIED);

        // set the time at what the account was updated
        account.setUpdatedAt(Instant.now());

        // send email to user after account has been verificated
        userVerificationService.sendConfirmedVerificationEmail(account);

        log.debug("UserAccount successfully verified.");
        return userRepository.save(account);
    }
}
