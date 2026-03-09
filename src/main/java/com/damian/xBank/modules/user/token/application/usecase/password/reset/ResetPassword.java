package com.damian.xBank.modules.user.token.application.usecase.password.reset;

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
 * It resets the user password using a token previously received using {@link RequestPasswordReset}
 * <p>
 * To set a new password the token must be valid.
 */
@Service
public class ResetPassword {
    private static final Logger log = LoggerFactory.getLogger(ResetPassword.class);
    private final UserTokenRepository userTokenRepository;
    private final UserTokenPasswordResetNotifier userTokenPasswordResetNotifier;
    private final UserTokenService userTokenService;
    private final UserPasswordService userPasswordService;

    public ResetPassword(
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
     * @param command the command with the password to set
     */
    @Transactional
    public void execute(ResetPasswordCommand command) {
        // verify the token
        final UserToken userToken = userTokenService.validateToken(command.token());

        log.debug("Resetting password for user: {} using a token.", userToken.getUser().getId());

        // update the password
        userPasswordService.updatePassword(userToken.getUser().getId(), command.password());

        // set the token as used
        userToken.markAsUsed();
        userTokenRepository.save(userToken);

        // send the email notifying the user that his password is successfully changed
        userTokenPasswordResetNotifier.notifyPasswordReset(userToken.getUser().getEmail());
        log.debug("Password reset successfully.");
    }
}
