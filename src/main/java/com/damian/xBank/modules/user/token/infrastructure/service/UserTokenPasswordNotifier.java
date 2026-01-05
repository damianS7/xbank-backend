package com.damian.xBank.modules.user.token.infrastructure.service;

public interface UserTokenPasswordNotifier {
    void sendPasswordResetToken(String toEmail, String link);

    void notifyPasswordReset(String toEmail);
}
