package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserAccountPasswordService;
import com.damian.xBank.modules.user.user.infrastructure.service.UserAccountVerificationService;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAccountPasswordUpdate {
    private static final Logger log = LoggerFactory.getLogger(UserAccountPasswordUpdate.class);
    private final Environment env;
    private final UserAccountPasswordService userAccountPasswordService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PasswordValidator passwordValidator;
    private final AuthenticationContext authenticationContext;
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailSenderService emailSenderService;
    private final UserAccountVerificationService userAccountVerificationService;
    private final UserAccountTokenService userAccountTokenService;

    public UserAccountPasswordUpdate(
            Environment env,
            UserAccountPasswordService userAccountPasswordService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            PasswordValidator passwordValidator,
            AuthenticationContext authenticationContext,
            UserAccountTokenRepository userAccountTokenRepository,
            UserAccountRepository userAccountRepository,
            EmailSenderService emailSenderService,
            UserAccountVerificationService userAccountVerificationService,
            UserAccountTokenService userAccountTokenService
    ) {
        this.env = env;
        this.userAccountPasswordService = userAccountPasswordService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.passwordValidator = passwordValidator;
        this.authenticationContext = authenticationContext;
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.emailSenderService = emailSenderService;
        this.userAccountVerificationService = userAccountVerificationService;
        this.userAccountTokenService = userAccountTokenService;
    }

    /**
     * It updates the password of the current user
     *
     * @param request the request body that contains the current password and the new password
     * @throws UserAccountNotFoundException                    if the user does not exist
     * @throws UserAccountInvalidPasswordConfirmationException if the password does not match
     */
    public void execute(UserAccountPasswordUpdateRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Before making any changes we check that the password sent by the user matches the one in the entity
        passwordValidator.validatePassword(currentUser, request.currentPassword());

        // update the password
        userAccountPasswordService.updatePassword(currentUser.getId(), request.newPassword());
    }


}
