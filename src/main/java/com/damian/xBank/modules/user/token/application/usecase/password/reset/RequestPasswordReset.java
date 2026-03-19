package com.damian.xBank.modules.user.token.application.usecase.password.reset;

import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenPasswordResetNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.notification.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Request a password reset token to be sent to the user email.
 * <p>
 * Email will contain a link to reset the password.
 */
@Service
public class RequestPasswordReset {
    private static final Logger log = LoggerFactory.getLogger(RequestPasswordReset.class);
    private final UserTokenPasswordResetNotifier userTokenPasswordResetNotifier;
    private final UserRepository userRepository;
    private final UserTokenLinkBuilder userTokenLinkBuilder;
    private final UserTokenRepository userTokenRepository;
    private final UserTokenFactory userTokenFactory;

    public RequestPasswordReset(
        UserTokenPasswordResetNotifier userTokenPasswordResetNotifier,
        UserRepository userRepository,
        UserTokenLinkBuilder userTokenLinkBuilder,
        UserTokenRepository userTokenRepository,
        UserTokenFactory userTokenFactory
    ) {
        this.userTokenPasswordResetNotifier = userTokenPasswordResetNotifier;
        this.userTokenLinkBuilder = userTokenLinkBuilder;
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.userTokenFactory = userTokenFactory;
    }

    /**
     * Generate a token for password reset.
     * Send email to the user with a link to reset password.
     *
     * @param command the command containing the email of the user and password
     */
    public void execute(RequestPasswordResetCommand command) {
        // generate a new password reset token
        log.debug("Generating password reset token for email: {}", command.email());
        User user = userRepository
            .findByEmail(command.email())
            .orElseThrow(
                () -> {
                    log.error(
                        "Failed to generate password reset token. No user found for: {}",
                        command.email()
                    );
                    return new UserNotFoundException(command.email());
                }
            );

        // generate the token for password reset
        UserToken token = userTokenFactory.passwordToken(user);
        userTokenRepository.save(token);

        // create the password reset link
        String link = userTokenLinkBuilder.buildPasswordResetLink(token.getToken());

        // send the email
        userTokenPasswordResetNotifier.sendPasswordResetToken(command.email(), link);

        log.debug("Password reset token generated and sent to: {}", command.email());
    }
}
