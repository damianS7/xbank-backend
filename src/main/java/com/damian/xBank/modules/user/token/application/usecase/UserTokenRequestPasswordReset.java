package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.dto.request.UserTokenRequestPasswordResetRequest;
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
public class UserTokenRequestPasswordReset {
    private static final Logger log = LoggerFactory.getLogger(UserTokenRequestPasswordReset.class);
    private final UserTokenPasswordResetNotifier userTokenPasswordResetNotifier;
    private final UserRepository userRepository;
    private final UserTokenLinkBuilder userTokenLinkBuilder;
    private final UserTokenRepository userTokenRepository;

    public UserTokenRequestPasswordReset(
            UserTokenPasswordResetNotifier userTokenPasswordResetNotifier,
            UserRepository userRepository,
            UserTokenLinkBuilder userTokenLinkBuilder,
            UserTokenRepository userTokenRepository
    ) {
        this.userTokenPasswordResetNotifier = userTokenPasswordResetNotifier;
        this.userTokenLinkBuilder = userTokenLinkBuilder;
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
    }

    /**
     * Generate a token for password reset.
     * Send email to the user with a link to reset password.
     *
     * @param request the request containing the email of the user and password
     */
    public void execute(UserTokenRequestPasswordResetRequest request) {
        // generate a new password reset token
        log.debug("Generating password reset token for email: {}", request.email());
        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(
                        () -> {
                            log.error(
                                    "Failed to generate password reset token. No user found for: {}",
                                    request.email()
                            );
                            return new UserNotFoundException(request.email());
                        }
                );

        // generate the token for password reset
        UserToken token = new UserToken(user);
        token.generateResetPasswordToken();

        userTokenRepository.save(token);

        // create the password reset link
        String link = userTokenLinkBuilder.buildPasswordResetLink(token.getToken());

        // send the email
        userTokenPasswordResetNotifier.sendPasswordResetToken(request.email(), link);

        log.debug("Password reset token generated and sent to: {}", request.email());
    }
}
