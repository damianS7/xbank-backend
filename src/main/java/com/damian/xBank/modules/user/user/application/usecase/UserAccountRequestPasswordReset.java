package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordResetRequest;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserAccountVerificationService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class UserAccountRequestPasswordReset {
    private static final Logger log = LoggerFactory.getLogger(UserAccountRequestPasswordReset.class);
    private final Environment env;
    private final UserTokenRepository userTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailSenderService emailSenderService;
    private final UserAccountVerificationService userAccountVerificationService;
    private final UserTokenService userTokenService;

    public UserAccountRequestPasswordReset(
            Environment env,
            UserTokenRepository userTokenRepository,
            UserAccountRepository userAccountRepository,
            EmailSenderService emailSenderService,
            UserAccountVerificationService userAccountVerificationService,
            UserTokenService userTokenService
    ) {
        this.env = env;
        this.userTokenRepository = userTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.emailSenderService = emailSenderService;
        this.userAccountVerificationService = userAccountVerificationService;
        this.userTokenService = userTokenService;
    }

    /**
     * Send email to the user with a link to reset password.
     *
     */
    public void execute(UserAccountPasswordResetRequest request) {
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
