package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.dto.request.UserTokenResetPasswordRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenPasswordResetNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.infrastructure.service.UserPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * It resets the user password using a token previously received using {@link UserTokenRequestPasswordReset}
 * <p>
 * To set a new password the token must be valid.
 */
@Service
public class UserTokenResetPassword {
    private static final Logger log = LoggerFactory.getLogger(UserTokenResetPassword.class);
    private final UserTokenRepository userTokenRepository;
    private final UserTokenPasswordResetNotifier userTokenPasswordResetNotifier;
    private final UserTokenService userTokenService;
    private final UserPasswordService userPasswordService;

    public UserTokenResetPassword(
            UserTokenRepository userTokenRepository,
            UserTokenPasswordResetNotifier userTokenPasswordResetNotifier,
            UserTokenService userTokenService,
            UserPasswordService userPasswordService
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userTokenPasswordResetNotifier = userTokenPasswordResetNotifier;
        this.userTokenService = userTokenService;
        this.userPasswordService = userPasswordService;
    }

    /**
     * It resets the user password using a token.
     *
     * @param token   the token used to reset the password
     * @param request the request with the password to set
     */
    @Transactional
    public void execute(String token, UserTokenResetPasswordRequest request) {
        // verify the token
        final UserToken userToken = userTokenService.validateToken(token);

        log.debug("Resetting password for user: {} using a token.", userToken.getUser().getId());

        // update the password
        userPasswordService.updatePassword(userToken.getUser().getId(), request.password());

        // set the token as used
        userToken.markAsUsed();
        userTokenRepository.save(userToken);

        // send the email notifying the user that his password is successfully changed
        userTokenPasswordResetNotifier.notifyPasswordReset(userToken.getUser().getEmail());
        log.debug("Password reset successfully.");
    }
}
