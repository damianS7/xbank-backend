package com.damian.xBank.modules.user.token.domain.notification;

public interface UserTokenPasswordResetNotifier {
    void sendPasswordResetToken(String toEmail, String link);

    void notifyPasswordReset(String toEmail);
}
