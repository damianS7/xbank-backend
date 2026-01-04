package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountVerificationResendRequest;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserAccountVerificationService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserAccountResendVerification {
    private static final Logger log = LoggerFactory.getLogger(UserAccountResendVerification.class);
    private final UserTokenRepository userTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailSenderService emailSenderService;
    private final UserAccountVerificationService userAccountVerificationService;
    private final UserTokenService userTokenService;

    public UserAccountResendVerification(
            UserTokenRepository userTokenRepository,
            UserAccountRepository userAccountRepository,
            EmailSenderService emailSenderService,
            UserAccountVerificationService userAccountVerificationService,
            UserTokenService userTokenService
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.emailSenderService = emailSenderService;
        this.userAccountVerificationService = userAccountVerificationService;
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

        userAccountVerificationService
                .sendVerificationLinkEmail(request.email(), userToken.getToken());
    }
}
