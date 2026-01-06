package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.domain.exception.UserTokenExpiredException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenNotFoundException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenUsedException;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Confirms the user account using a token previously received
 * by using {@link UserTokenRequestVerification} or by new registration
 */
@Service
public class UserTokenVerifyAccount {
    private static final Logger log = LoggerFactory.getLogger(UserTokenVerifyAccount.class);
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final UserTokenService userTokenService;
    private final UserTokenVerificationNotifier userTokenVerificationNotifier;

    public UserTokenVerifyAccount(
            UserTokenRepository userTokenRepository,
            UserRepository userRepository,
            UserTokenService userTokenService,
            UserTokenVerificationNotifier userTokenVerificationNotifier
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
        this.userTokenService = userTokenService;
        this.userTokenVerificationNotifier = userTokenVerificationNotifier;
    }

    /**
     * Verify a user account using a valid token
     *
     * @param token the token to validate.
     * @throws UserTokenNotFoundException          when the token not exists in database
     * @throws UserTokenExpiredException           when the token exists but its expired
     * @throws UserTokenUsedException              when the token exists but already used.
     * @throws UserVerificationNotPendingException when the user is not pending for activation.
     */
    @Transactional
    public void execute(String token) {
        // check the token is valid and not expired.
        UserToken userToken = userTokenService.validateToken(token);

        // Get the user that owns the token
        User user = userToken.getUser();

        // checks if the user is pending for verification
        user.verifyAccount();

        // mark the token as used
        userToken.markAsUsed();

        userTokenRepository.save(userToken);
        userRepository.save(user);

        // send email to user after user has been verificated
        userTokenVerificationNotifier.notifyVerification(user.getEmail());

        log.debug("User: {} successfully verified.", user.getId());
    }
}
