package com.damian.xBank.modules.user.token.infrastructure.service;

import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.springframework.stereotype.Component;

@Component
public class UserTokenEmailVerificationNotifier implements UserTokenVerificationNotifier {

    private final EmailSenderService emailSender;

    public UserTokenEmailVerificationNotifier(EmailSenderService emailSender) {
        this.emailSender = emailSender;
    }
    
    /**
     * It sends an email with a verification link
     *
     * @param toEmail The email address to send the verification link to.
     * @param link
     */
    @Override
    public void sendVerificationToken(String toEmail, String link) {
        emailSender.send(
                toEmail,
                "xBank account verification link.",
                "Please click on the link below to confirm your registration: \n\n" + link
        );
    }

    /**
     * It sends a welcome message to the user email address after verification.
     *
     * @param toEmail
     */
    @Override
    public void notifyVerification(String toEmail) {
        emailSender.send(
                toEmail,
                "Welcome to xBank!",
                "Your account has been verified successfully."
        );
    }
}