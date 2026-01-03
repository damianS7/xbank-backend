package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.account.token.domain.model.UserAccountToken;
import com.damian.xBank.modules.user.account.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.infrastructure.service.UserAccountTokenService;
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
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailSenderService emailSenderService;
    private final UserAccountVerificationService userAccountVerificationService;
    private final UserAccountTokenService userAccountTokenService;

    public UserAccountResendVerification(
            UserAccountTokenRepository userAccountTokenRepository,
            UserAccountRepository userAccountRepository,
            EmailSenderService emailSenderService,
            UserAccountVerificationService userAccountVerificationService,
            UserAccountTokenService userAccountTokenService
    ) {
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.emailSenderService = emailSenderService;
        this.userAccountVerificationService = userAccountVerificationService;
        this.userAccountTokenService = userAccountTokenService;
    }

    /**
     * It sends an email with a verification link
     *
     */
    public void execute(UserAccountVerificationResendRequest request) {

        // generate a new verification token
        UserAccountToken userAccountToken = userAccountTokenService
                .generateVerificationToken(request.email());

        userAccountVerificationService
                .sendVerificationLinkEmail(request.email(), userAccountToken.getToken());
    }
}
