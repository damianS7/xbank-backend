package com.damian.xBank.modules.user.token.infrastructure.service.notification;

import com.damian.xBank.modules.user.token.domain.notification.UserTokenPasswordResetNotifier;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.springframework.stereotype.Component;

@Component
public class UserTokenEmailPasswordResetNotifier implements UserTokenPasswordResetNotifier {

    private final EmailSenderService emailSender;

    public UserTokenEmailPasswordResetNotifier(EmailSenderService emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendPasswordResetToken(String toEmail, String link) {
        emailSender.send(
                toEmail,
                "Reset your password",
                "Reset your password using this link: " + link
        );
    }

    @Override
    public void notifyPasswordReset(String toEmail) {
        emailSender.send(
                toEmail,
                "xBank account: password reset successfully.",
                "Your password has been reset."
        );
    }
}