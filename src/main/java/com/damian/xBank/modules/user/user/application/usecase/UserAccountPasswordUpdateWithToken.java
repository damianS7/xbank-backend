package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.account.token.domain.model.UserAccountToken;
import com.damian.xBank.modules.user.account.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.xBank.modules.user.user.infrastructure.service.UserAccountPasswordService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserAccountPasswordUpdateWithToken {
    private static final Logger log = LoggerFactory.getLogger(UserAccountPasswordUpdateWithToken.class);
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final EmailSenderService emailSenderService;
    private final UserAccountTokenService userAccountTokenService;
    private final UserAccountPasswordService userAccountPasswordService;

    public UserAccountPasswordUpdateWithToken(
            UserAccountTokenRepository userAccountTokenRepository,
            EmailSenderService emailSenderService,
            UserAccountTokenService userAccountTokenService, UserAccountPasswordService userAccountPasswordService
    ) {
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.emailSenderService = emailSenderService;
        this.userAccountTokenService = userAccountTokenService;
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
        final UserAccountToken userAccountToken = userAccountTokenService.validateToken(token);

        // update the password
        userAccountPasswordService.updatePassword(userAccountToken.getAccount().getId(), request.password());

        // set the token as used
        userAccountToken.setUsed(true);
        userAccountTokenRepository.save(userAccountToken);

        // send the email notifying the user that his password is successfully changed
        this.sendResetPasswordSuccessEmail(userAccountToken.getAccount().getEmail());
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
