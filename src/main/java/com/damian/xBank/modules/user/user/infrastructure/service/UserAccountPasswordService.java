package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.account.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.infrastructure.mail.EmailSenderService;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountPasswordService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountPasswordService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserAccountRepository userAccountRepository;
    private final PasswordValidator passwordValidator;
    private final EmailSenderService emailSenderService;
    private final UserAccountTokenRepository userAccountTokenRepository;
    private final Environment env;
    private final UserAccountTokenService userAccountTokenService;
    private final AuthenticationContext authenticationContext;

    public UserAccountPasswordService(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserAccountRepository userAccountRepository,
            PasswordValidator passwordValidator,
            EmailSenderService emailSenderService,
            UserAccountTokenRepository userAccountTokenRepository,
            Environment env,
            UserAccountTokenService userAccountTokenService,
            AuthenticationContext authenticationContext
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userAccountRepository = userAccountRepository;
        this.passwordValidator = passwordValidator;
        this.emailSenderService = emailSenderService;
        this.userAccountTokenRepository = userAccountTokenRepository;
        this.env = env;
        this.authenticationContext = authenticationContext;
        this.userAccountTokenService = userAccountTokenService;
    }


    /**
     * It updates the password of given user.
     *
     * @param userId   the id of the user to be updated
     * @param password the new password to be set
     * @throws UserAccountNotFoundException                    if the user does not exist
     * @throws UserAccountInvalidPasswordConfirmationException if the password does not match
     */
    public void updatePassword(Long userId, String password) {
        // we get the UserAuth entity so we can save.
        User user = userAccountRepository.findById(userId).orElseThrow(
                () -> {
                    log.warn("Failed to update password");
                    return new UserAccountNotFoundException(userId);
                }
        );

        // set the new password
        user.setPassword(bCryptPasswordEncoder.encode(password));

        // we change the updateAt timestamp field
        user.setUpdatedAt(Instant.now());

        // save the changes
        userAccountRepository.save(user);
        log.debug("Successfully updated password for user: {}", userId);
    }
}
