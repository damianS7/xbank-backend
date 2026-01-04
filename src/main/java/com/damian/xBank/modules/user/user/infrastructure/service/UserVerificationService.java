package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationService {
    private static final Logger log = LoggerFactory.getLogger(UserVerificationService.class);
    private final Environment env;
    private final EmailSenderService emailSenderService;

    public UserVerificationService(
            Environment env,
            EmailSenderService emailSenderService
    ) {
        this.env = env;
        this.emailSenderService = emailSenderService;
    }

    /**
     * It sends a welcome message to the user email address after verification.
     *
     * @param user The user to send a welcome message to.
     */
    public void sendConfirmedVerificationEmail(User user) {
        emailSenderService.send(
                user.getEmail(),
                "Welcome to Photogram!",
                "Your account has been verified successfully."
        );
    }

    /**
     * It sends an email with a verification link
     *
     * @param email The email address to send the verification link to.
     * @param token The token that will be used to verify the user's account.
     */
    public void sendVerificationLinkEmail(String email, String token) {
        String host = env.getProperty("app.frontend.host");
        String port = env.getProperty("app.frontend.port");
        String url = String.format("http://%s:%s", host, port);
        String activationLink = url + "/customers/accounts/verification/" + token;

        // Send email to confirm registration
        emailSenderService.send(
                email,
                "xBank account verification link.",
                "Please click on the link below to confirm your registration: \n\n" + activationLink
        );
    }
}
