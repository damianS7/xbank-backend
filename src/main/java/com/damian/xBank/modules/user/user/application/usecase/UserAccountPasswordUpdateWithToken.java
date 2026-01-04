package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.xBank.modules.user.user.infrastructure.service.UserAccountPasswordService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserAccountPasswordUpdateWithToken {
    private static final Logger log = LoggerFactory.getLogger(UserAccountPasswordUpdateWithToken.class);
    private final UserTokenRepository userTokenRepository;
    private final EmailSenderService emailSenderService;
    private final UserTokenService userTokenService;
    private final UserAccountPasswordService userAccountPasswordService;

    public UserAccountPasswordUpdateWithToken(
            UserTokenRepository userTokenRepository,
            EmailSenderService emailSenderService,
            UserTokenService userTokenService, UserAccountPasswordService userAccountPasswordService
    ) {
        this.userTokenRepository = userTokenRepository;
        this.emailSenderService = emailSenderService;
        this.userTokenService = userTokenService;
        this.userAccountPasswordService = userAccountPasswordService;
    }

    /**
     * It resets the user password using a token.
     *
     * @param token   the token used to reset the password
     * @param request the request with the password to set
     */
    public void execute(String token, UserAccountPasswordResetSetRequest request) {
        log.debug("Resetting password using a token.");
        // verify the token
        final UserToken userToken = userTokenService.validateToken(token);

        // update the password
        userAccountPasswordService.updatePassword(userToken.getUser().getId(), request.password());

        // set the token as used
        userToken.setUsed(true);
        userTokenRepository.save(userToken);

        // send the email notifying the user that his password is successfully changed
        this.sendResetPasswordSuccessEmail(userToken.getUser().getEmail());
        log.debug("Password reset successfully.");
    }

    /**
     * Send an email after successfully reset of the password
     *
     * @param toEmail the user's email address to send the email
     */
    public void sendResetPasswordSuccessEmail(String toEmail) {
        emailSenderService.send(
                toEmail,
                "Photogram account: password reset successfully.",
                "Your password has been reset."
        );
    }

}
