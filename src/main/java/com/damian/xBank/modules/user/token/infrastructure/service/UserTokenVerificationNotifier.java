package com.damian.xBank.modules.user.token.infrastructure.service;

public interface UserTokenVerificationNotifier {
    void sendVerificationToken(String toEmail, String link);

    void notifyVerification(String toEmail);
}
