package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.dto.request.UserAccountVerificationResendRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserVerificationService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserTokenResendVerification {
    private static final Logger log = LoggerFactory.getLogger(UserTokenResendVerification.class);
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final UserVerificationService userVerificationService;
    private final UserTokenService userTokenService;

    public UserTokenResendVerification(
            UserTokenRepository userTokenRepository,
            UserRepository userRepository,
            EmailSenderService emailSenderService,
            UserVerificationService userVerificationService,
            UserTokenService userTokenService
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.userVerificationService = userVerificationService;
        this.userTokenService = userTokenService;
    }

    /**
     * It sends an email with a verification link
     *
     */
    public void execute(UserAccountVerificationResendRequest request) {

        // generate a new verification token
        UserToken userToken = userTokenService
                .generateVerificationToken(request.email());

        userVerificationService
                .sendVerificationLinkEmail(request.email(), userToken.getToken());
    }
}
