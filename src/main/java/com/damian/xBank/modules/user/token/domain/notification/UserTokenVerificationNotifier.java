package com.damian.xBank.modules.user.token.domain.notification;

public interface UserTokenVerificationNotifier {
    void sendVerificationToken(String toEmail, String link);

    void notifyVerification(String toEmail);
}
