package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserVerificationService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class UserTokenRequestPasswordReset {
    private static final Logger log = LoggerFactory.getLogger(UserTokenRequestPasswordReset.class);
    private final Environment env;
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final UserVerificationService userVerificationService;
    private final UserTokenService userTokenService;

    public UserTokenRequestPasswordReset(
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
     * Send email to the user with a link to reset password.
     *
     */
    public void execute(UserPasswordResetRequest request) {
        // generate a new password reset token
        UserToken userToken = userTokenService.generatePasswordResetToken(request);

        String host = env.getProperty("app.frontend.host");
        String port = env.getProperty("app.frontend.port");
        String url = String.format("http://%s:%s", host, port);
        String link = url + "/accounts/password/reset/" + userToken.getToken();
        emailSenderService.send(
                request.email(),
                "Photogram account: Password reset request.",
                "Reset your password following this url: " + link
        );
    }

}
